package com.pivot.aham.api.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.pivot.aham.api.service.job.AccountFundNavJob;
import com.pivot.aham.api.service.job.interevent.CalFundNavEvent;
import com.pivot.aham.api.service.job.interevent.NormalAccountFeeCreateEvent;
import com.pivot.aham.api.service.job.interevent.NormalClientFeeReduceEvent;
import com.pivot.aham.api.service.job.wrapperbean.AccountFundNavWrapperBean;
import com.pivot.aham.api.service.job.wrapperbean.AccountRedeemWrapperBean;
import com.pivot.aham.api.service.job.wrapperbean.AccountTpcfTncfBean;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.enums.analysis.*;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.api.service.support.AccountAssetStatisticBean;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.AccountTypeEnum;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.RedeemTypeEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Created by luyang.li on 18/12/17.
 */
@ElasticJobConf(name = "AssetFundNavJob_2",
        cron = "0 30 19 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易03_交易分析#计算自建基金净值", eventTraceRdbDataSource = "dataSource")
@Slf4j
@Component
public class AssetFundNavJobImpl implements SimpleJob, AccountFundNavJob {

    @Resource
    private AccountAssetService accountAssetService;
    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private AssetFundNavService assetFundNavService;
    @Autowired
    private AccountRedeemService accountRedeemService;
    @Autowired
    private RedeemApplyService redeemApplyService;
    @Resource
    private AccountUserService accountUserService;
    @Resource
    private AssetFundNavService accountNavService;
    @Resource
    private UserFundNavService userFundNavService;
    @Resource
    private AnalysisSupportService analysisSupportService;
    @Resource
    private EventBus eventBus;
    @Resource
    private AccountDividendService accountDividendService;
    @Resource
    private UserDividendService userDividendService;
    @Resource
    private TpcfTncfService tpcfTncfService;
    @Resource
    private UserBatchNavService userBatchNavService; //Added By WooiTatt
    @Resource
    private PivotFeeDetailService pivotFeeDetailService; //Added By WooiTatt
    @Resource
    private AccountPerformanceFeeService accountPerformanceFeeService; //Added By WooiTatt
    @Resource
    private AccountPerformanceFeeDetailsService accountPerformanceFeeDetailsService; //Added By WooiTatt

    private static final BigDecimal COMPARE_DOT = new BigDecimal("0.0001");
    
    private static final String strStartDatePF = "2020-05-01"; //Added By WooiTatt
    
    @Resource
    private WithdrawalNotifyToAham withdrawalNotifyToAham;

    public void calculateAssetFundNav() {
       calculateAssetFundNav(DateUtils.now(), null);
       withdrawalNotifyToAham.withdrawalSaxoToUob();
    }

    /**
     * 计算自建基金的净值,针对于每一个Account
     * <p>
     * 0、initial day ==0.1、计算资产 ==0.2、基金净值是 1 ==0.3、计算份额
     * <p>
     * 1、正负现金流都为0 ==1.1、当日总资产: Fund_Asset(t) = Total Cash + Total Equity
     * ==1.2、当日总份额: FundShares(t) = ADJ_FundShares(t-1) ==1.3、当日净值: FundNAV(t)=
     * Fund_Asset(t) / FundShares(t)
     * <p>
     * 2、只有正现金流: TPCF ==2.1、当日总资产: Fund_Asset(t) = Total Cash + Total Equity
     * ==2.2、当日净值: FundNAV(t)= (Fund_Asset(t)-TPCF(t)) / ADJ_FundShares(t-1)
     * ==2.3、放入份额: FundShares(t) = ADJ_FundShares(t-1)+TPCF(t) / FundNAV(t)
     * <p>
     * 3、只有负现金流: TNCF(该值为负值) ==3.1、临时资产: Fund_Asset(t) = Total Cash + Total
     * Equity ==3.2、临时份额: FundShares(t) = ADJ_FundShares(t-1) ==3.2、当日净值:
     * FundNAV(t)= Fund_Asset(t) / FundShares(t) ==3.3、当日份额: ADJ_FundShares(t)=
     * FundShares(t)+ TNCF(t)/ FundNAV(t-1) ==3.4、当日现金: ADJ_Cash_SAXO_USD(t)=
     * Cash_SAXO_USD(t) + Cash_Wihdraw(t) ==3.5、当日资产: ADJ_Fund_Asset(t) =
     * ADJ_Cash_SAXO_USD(t) + Current_(EV_USD(t) )
     * <p>
     * 4、既有正现金流和负现金流
     * <p>
     * 1、计算当日资产: Fund_Asset(t) = Total Cash + Total Equity 2、当日计算净值 = (当日资产 -
     * 正向资金流) / 昨日份额: (Fund_Asset(t)-TPCF(t)) / ADJ_FundShares(t-1) 3、临时份额 =
     * 昨日份额 + (正像现金流 / 当日净值) : FundShares(t) = ADJ_FundShares(t-1)+TPCF(t) /
     * FundNAV(t) 4、当日份额 = 临时份额 + (负现金流(该值为负数) / 昨日净值); ADJ_FundShares(t)=
     * FundShares(t)+ TNCF(t)/ FundNAV(t-1) 5、当日资产 = cash + 提现资产流(该值为负值) +
     * Current_(EV_USD(t) ) ***(这个值理论和统记的总资产相同)
     *
     * @param date
     */
    @Override
    public void calculateAssetFundNav(Date date, Long accountId) {
        log.info("accountId:{},开始计算NAV", accountId);
        Date assetEndTime = DateUtils.dayEnd(date);
        Date yesterday = DateUtils.addDateByDay(date, -1);
        //查询所etf的收市价格
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
        log.info("{},收市价:{}", yesterday, etfClosingPriceMap);
        yesterday = DateUtils.addDateByDay(date, -1);
        if (null == etfClosingPriceMap || etfClosingPriceMap.size() == 0) {
            ErrorLogAndMailUtil.logError(log, "本日收市价为空");
            return;
        }
        //统计每个assetFund上的基金净值
        AccountInfoPO po = new AccountInfoPO();
        po.setCreateTime(assetEndTime);
        List<AccountInfoPO> accountInfoPOList = accountInfoService.listAccountInfos(po);
        for (AccountInfoPO accountInfoPO : accountInfoPOList) {
            try {
                if (accountId != null && !accountId.equals(accountInfoPO.getId())) {
                    continue;
                }
                //发事件,获取昨日fee记录，增加各种fee的资产流水(扣减:管理费、监管费、附加税费)
                NormalClientFeeReduceEvent normalFeeReduceEvent = new NormalClientFeeReduceEvent();
                normalFeeReduceEvent.setAccountId(accountInfoPO.getId());
                normalFeeReduceEvent.setDate(date);
                eventBus.post(normalFeeReduceEvent);

                log.info("accountId:{},fee计算完毕，开始计算NAV", accountInfoPO.getId());
                AccountAssetPO queryParam = new AccountAssetPO();
                queryParam.setAccountId(accountInfoPO.getId());
                queryParam.setCreateEndTime(assetEndTime);
                List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
                if (CollectionUtils.isEmpty(accountAssetPOs)) {
                    log.info("进行基金净值计算,用户资产统计,该账户没有资产,不做处理。accountId:{}", accountInfoPO.getId());
                    continue;
                }
                //查询该账号上的总资产
                BigDecimal totalAssetBeforeWithdrawal = BigDecimal.ZERO;
                BigDecimal cashHolding = BigDecimal.ZERO;
                BigDecimal totalEquit = BigDecimal.ZERO;
                List<AccountAssetStatisticBean> accountAssetStatisticBeans = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
                //过滤出持有中的资产
                for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeans) {
                    if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING) {
                        totalAssetBeforeWithdrawal = totalAssetBeforeWithdrawal.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
                        if (accountAssetStatisticBean.getProductCode().equals(Constants.CASH)) {
                            cashHolding = cashHolding.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
                        } else {
                            totalEquit = totalEquit.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
                        }
                    }
                }

                log.info("accountId:{},计算NAV，总资产:{}, cashHolding:{}, totalEquit:{}", accountInfoPO.getId(), totalAssetBeforeWithdrawal, cashHolding, totalEquit);
                if (totalAssetBeforeWithdrawal.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                boolean initDay = InitDayEnum.INIT_DAY == accountInfoPO.getInitDay();

                //非首单
                AccountFundNavPO queryFundNav = new AccountFundNavPO();
                queryFundNav.setAccountId(accountInfoPO.getId());
                queryFundNav.setNavTime(DateUtils.dayStart(yesterday));
                AccountFundNavPO yesterdayFundNavPO = assetFundNavService.selectOne(queryFundNav);
                if (!initDay && yesterdayFundNavPO == null) {
                    log.error("账户{},昨日净值为空", accountInfoPO.getId());
                    continue;
                }

                log.info("===========account开始计算自建基金净值:{}===========", accountInfoPO.getId());
                AccountFundNavPO todayAccountFundNav = handelAccountFundNav(accountInfoPO, totalAssetBeforeWithdrawal, initDay,
                        yesterdayFundNavPO, date, cashHolding, totalEquit, etfClosingPriceMap);
                log.info("===========account计算自建基金净值结束:{},todayAccountFundNav:{}===========", accountInfoPO.getId(), JSON.toJSONString(todayAccountFundNav));
                if (todayAccountFundNav.getTotalShare().compareTo(BigDecimal.ZERO) <= 0) {
                    //账户资产为0 表示就是initDay  设置状态为首单
                    accountInfoPO.setInitDay(InitDayEnum.INIT_DAY);
                    accountInfoService.updateOrInsert(accountInfoPO);
                    List<AccountRedeemPO> accountRedeemPOS = tpcfTncfService.getAccountTncf(accountInfoPO.getId());
                    analysisSupportService.handleTncfSuccess(accountRedeemPOS);
                    //没有资产的时候不进行fee创建
//                   NormalAccountFeeCreateEvent normalAccountFeeCreateEvent = new NormalAccountFeeCreateEvent();
//                    normalAccountFeeCreateEvent.setAccountId(accountInfoPO.getId());
//                    normalAccountFeeCreateEvent.setTotalAsset(todayAccountFundNav.getTotalAsset());
//                    normalAccountFeeCreateEvent.setDate(date);
//                    eventBus.post(normalAccountFeeCreateEvent);
                    continue;
                }

                //用户计算NAV的时候也要计算分红
                log.info("===========account开始计算用户基金净值:{}===========", accountInfoPO.getId());
                List<UserFundNavPO> userFundNavPOList = null;
                if (initDay) {
                    userFundNavPOList = handelUserFundShareInitDay(date, todayAccountFundNav);
                } else {
                    userFundNavPOList = handelUserFundShare(date, todayAccountFundNav, yesterdayFundNavPO);
                }
                log.info("===========account计算用户基金净值:{},userFundNavPOList:{}===========", accountInfoPO.getId(), JSON.toJSONString(userFundNavPOList));

                if (CollectionUtils.isEmpty(userFundNavPOList)) {
                    continue;
                }
                log.info("===========account开始计算用户资产:{}===========", accountInfoPO.getId());
                List<UserAssetPO> userAssetPOs = handelUserAsset(assetEndTime, todayAccountFundNav, userFundNavPOList, etfClosingPriceMap);
                log.info("===========account计算用户资产结束:{},userAssetPOs:{}===========", accountInfoPO.getId(), JSON.toJSONString(userAssetPOs));

                AccountTpcfTncfBean accountTpcfTncfBean = getsAccountTpcfTncf(accountInfoPO.getId());
                accountInfoPO.setInitDay(InitDayEnum.UN_INIT_DAY);
                analysisSupportService.handelFundNavAndUserAsset(todayAccountFundNav, userFundNavPOList, userAssetPOs,
                        accountInfoPO, accountTpcfTncfBean);

                //发事件  监管费(0.0006),管理费(0.005),附加税费(0.07)
                NormalAccountFeeCreateEvent normalAccountFeeCreateEvent = new NormalAccountFeeCreateEvent();
                normalAccountFeeCreateEvent.setAccountId(accountInfoPO.getId());
                normalAccountFeeCreateEvent.setTotalAsset(todayAccountFundNav.getTotalAsset());
                normalAccountFeeCreateEvent.setDate(date);
                eventBus.post(normalAccountFeeCreateEvent);

            } catch (Exception ex) {
                log.error("账户:{},日期:{},计算计算自建基金净值和总份额、用户持有自建基金份额、用户持有etf份额异常:", accountInfoPO.getId(), DateUtils.getDate(), ex);
            }
        }
    }

    /**
     * 查询账户的Tpcf(recharge + dividend)和tncf
     *
     * @param accountId
     * @return
     */
    private AccountTpcfTncfBean getsAccountTpcfTncf(Long accountId) {
        AccountTpcfTncfBean accountTpcfTncfBean = new AccountTpcfTncfBean();
        //tncf
        List<AccountRedeemPO> accountRedeemPOS = tpcfTncfService.getAccountTncf(accountId);
        //Tpcf
        //List<AccountRechargePO> accountRechargePOS = tpcfTncfService.getAccountTpcf(accountId);
        List<AccountRechargePO> accountRechargePOS = tpcfTncfService.getAccountTpcfWithAssetComplete(accountId);
        //account_dividend
        AccountDividendPO accountDividendParam = new AccountDividendPO();
        accountDividendParam.setAccountId(accountId);
        accountDividendParam.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        List<AccountDividendPO> accountDividendPOS = accountDividendService.listAccountDividend(accountDividendParam);
        //user_dividend
        UserDividendPO userDividendParam = new UserDividendPO();
        userDividendParam.setAccountId(accountId);
        userDividendParam.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        List<UserDividendPO> userDividendPOS = userDividendService.listUserDividend(userDividendParam);

        accountTpcfTncfBean.setAccountDividendPOS(accountDividendPOS);
        accountTpcfTncfBean.setAccountRechargePOS(accountRechargePOS);
        accountTpcfTncfBean.setAccountRedeemPOs(accountRedeemPOS);
        accountTpcfTncfBean.setUserDividendPOS(userDividendPOS);
        return accountTpcfTncfBean;
    }

    @Override
    public void calculateAssetFundNavByDate(String date, Long accountId) {
        calculateAssetFundNav(DateUtils.parseDate(date), accountId);
    }

    /**
     * initDay处理用户在自建基金上的占比。
     *
     * @param date
     * @param todayFundNav
     * @return
     */
    private List<UserFundNavPO> handelUserFundShareInitDay(Date date, AccountFundNavPO todayFundNav) {
        List<UserFundNavPO> userFundNavPOList = Lists.newArrayList();

        BigDecimal totalRechargeMoney = BigDecimal.ZERO;
        Map<String, BigDecimal> userRechargeMoneyMap = Maps.newHashMap();
        AccountUserPO po = new AccountUserPO();
        po.setAccountId(todayFundNav.getAccountId());
        List<AccountUserPO> accountUserPOList = accountUserService.listByAccountUserPo(po);
        for (AccountUserPO accountUserPO : accountUserPOList) {
            BigDecimal userTpcf = getUserTdpcf(accountUserPO);
            if (userTpcf.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal money = userRechargeMoneyMap.get(accountUserPO.getClientId());
            if (null == money) {
                money = userTpcf;
            } else {
                money = money.add(userTpcf);
            }

            String key = accountUserPO.getClientId() + "_" + accountUserPO.getGoalId();
            userRechargeMoneyMap.put(key, money);

            totalRechargeMoney = totalRechargeMoney.add(money);
        }

        //分配基金份额占比
        for (String clientIdGoalId : userRechargeMoneyMap.keySet()) {
            BigDecimal money = userRechargeMoneyMap.get(clientIdGoalId);
            BigDecimal percent = money.divide(totalRechargeMoney, 6, BigDecimal.ROUND_DOWN);
            if (percent.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("用户clientId_goalId:{},充值金额money:{},比总金额totalMoney:{},百分比小于6位小时,不记录share",
                        clientIdGoalId, money, totalRechargeMoney);
                continue;
            }

            List<String> clientIdGoalIds = Splitter.on("_").splitToList(clientIdGoalId);
            BigDecimal rechargeShare = percent.multiply(todayFundNav.getTotalShare()).setScale(6, BigDecimal.ROUND_DOWN);
            UserFundNavPO todayUserFundNav = constractUserFundNav(rechargeShare, todayFundNav.getFundNav(),
                    todayFundNav.getAccountId(), clientIdGoalIds.get(0), clientIdGoalIds.get(1), date, todayFundNav.getTotalAsset());
            userFundNavPOList.add(todayUserFundNav);
            
            //added by wooitatt
            //if (!PropertiesUtil.isProd()) {
                log.info("#Start User Recharge Batch No (handelUserFundShareInitDay)");
                insertUserBatchFundNav(todayUserFundNav, rechargeShare);
            //}
        }

        return userFundNavPOList;
    }

    private BigDecimal getUserTdpcf(AccountUserPO accountUserPO) {
        BigDecimal userTpcf = tpcfTncfService.getUserTpcfMoney(accountUserPO);

        UserDividendPO userDividendParam = new UserDividendPO();
        userDividendParam.setAccountId(accountUserPO.getAccountId());
        userDividendParam.setGoalId(accountUserPO.getGoalId());
        userDividendParam.setClientId(accountUserPO.getClientId());
        userDividendParam.setHandelType(DividendHandelTypeEnum.USED_NAV);
        userDividendParam.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        BigDecimal userDividendMoney = userDividendService.getUserDividendMoney(userDividendParam);
        BigDecimal navTpcfMoney = userTpcf.add(userDividendMoney).setScale(6, BigDecimal.ROUND_HALF_UP);
        log.info("getUserTdpcf,userTpcf:{},userDividendMoney:{},navTpcfMoney:{}", userTpcf, userDividendMoney, navTpcfMoney);
        return navTpcfMoney;
    }

    /**
     * 统计用户资产 : 用户的持有share占总share的百分比乘以账户持有的etf,即用户持有的etf
     *
     * @param assetEndTime
     * @param accountFundNav
     * @param userFundNavPOList
     * @param etfClosingPriceMap
     */
    private List<UserAssetPO> handelUserAsset(Date assetEndTime,
            AccountFundNavPO accountFundNav,
            List<UserFundNavPO> userFundNavPOList,
            Map<String, BigDecimal> etfClosingPriceMap) {
        AccountAssetPO queryParam = new AccountAssetPO();
        queryParam.setAccountId(accountFundNav.getAccountId());
        queryParam.setCreateEndTime(assetEndTime);
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
        List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
        //过滤出持有中的资产
        Map<String, AccountAssetStatisticBean> allHoldProductMap = Maps.newHashMap();
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeens) {
            if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING) {
                allHoldProductMap.put(accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean);
            }
        }

        List<UserAssetPO> userAssetPOs = Lists.newArrayList();

        for (UserFundNavPO userFundNav : userFundNavPOList) {
            BigDecimal cashAmount = BigDecimal.ZERO;
            BigDecimal percent = userFundNav.getTotalShare().divide(accountFundNav.getTotalShare(), 6, BigDecimal.ROUND_DOWN);

            //统计账户层面的etf 计算用户层面的etf
            for (String productCode : allHoldProductMap.keySet()) {
                AccountAssetStatisticBean productAsset = allHoldProductMap.get(productCode);
                BigDecimal etfPrice = BigDecimal.ZERO;
                BigDecimal etfAmount = BigDecimal.ZERO;
                if (productCode.equals(Constants.CASH) || productCode.equals(Constants.UN_BUY_PRODUCT_CODE)) {
                    etfAmount = productAsset.getProductMoney().multiply(percent).setScale(6, BigDecimal.ROUND_DOWN);
                    cashAmount = cashAmount.add(etfAmount);
                    etfPrice = BigDecimal.ONE;
                } else {
                    etfAmount = productAsset.getProductShare().multiply(percent).setScale(6, BigDecimal.ROUND_DOWN);
                    etfPrice = etfClosingPriceMap.get(productCode);
                }
                if (etfAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                // ByPass to insert the cash and unbuy first , sum the amount together then insert;
                if (productCode.equals(Constants.CASH) || productCode.equals(Constants.UN_BUY_PRODUCT_CODE)); else {
                    UserAssetPO userAssetPO = new UserAssetPO();
                    userAssetPO.setId(Sequence.next());
                    userAssetPO.setAccountId(accountFundNav.getAccountId());
                    userAssetPO.setAssetTime(DateUtils.dayStart(assetEndTime));
                    userAssetPO.setClientId(userFundNav.getClientId());
                    userAssetPO.setProductCode(productCode);
                    userAssetPO.setShare(etfAmount);
                    userAssetPO.setMoney(etfAmount.multiply(etfPrice).setScale(6, BigDecimal.ROUND_DOWN));
                    userAssetPO.setCreateTime(DateUtils.now());
                    userAssetPO.setUpdateTime(DateUtils.now());
                    userAssetPO.setGoalId(userFundNav.getGoalId());
                    userAssetPOs.add(userAssetPO);
                }
            }
            // After Looping should get the total Unbuy and cash sum up, then insert into DB;
            UserAssetPO userAssetPO = new UserAssetPO();
            userAssetPO.setId(Sequence.next());
            userAssetPO.setAccountId(accountFundNav.getAccountId());
            userAssetPO.setAssetTime(DateUtils.dayStart(assetEndTime));
            userAssetPO.setClientId(userFundNav.getClientId());
            userAssetPO.setProductCode(Constants.CASH);
            userAssetPO.setShare(cashAmount);
            userAssetPO.setMoney(cashAmount.multiply(BigDecimal.ONE).setScale(6, BigDecimal.ROUND_DOWN));
            userAssetPO.setCreateTime(DateUtils.now());
            userAssetPO.setUpdateTime(DateUtils.now());
            userAssetPO.setGoalId(userFundNav.getGoalId());
            userAssetPOs.add(userAssetPO);
        }

        return userAssetPOs;
    }

    /**
     * 计算用户层面的基金净值
     *
     * @param date
     * @param todayNav
     * @param yesAccountFundNav
     */
    private List<UserFundNavPO> handelUserFundShare(Date date, AccountFundNavPO todayFundNav, AccountFundNavPO yesAccountFundNav) {
        Date yesDate = DateUtils.addDays(date, -1);
        List<UserFundNavPO> userFundNavPOList = Lists.newArrayList();

        AccountUserPO po = new AccountUserPO();
        po.setAccountId(yesAccountFundNav.getAccountId());
        List<AccountUserPO> accountUserPOList = accountUserService.listByAccountUserPo(po);
        log.info("accountId:{},计算用户自建基金净值查询的用户账户关系:{}", yesAccountFundNav.getAccountId(), JSON.toJSONString(accountUserPOList));
        for (AccountUserPO accountUserPO : accountUserPOList) {
//            BigDecimal recahrgeMoney = rechargeService.getUserRechargeMoney(accountUserPO, startTime, endTime);
            BigDecimal userTpcf = getUserTdpcf(accountUserPO);
            BigDecimal rechargeShare = userTpcf.divide(todayFundNav.getFundNav(), 6, BigDecimal.ROUND_DOWN);

            //获取TNCF
            BigDecimal tncfShares = tpcfTncfService.getUserTncfShares(accountUserPO);

            UserFundNavPO userFundNavPO = new UserFundNavPO();
            userFundNavPO.setClientId(accountUserPO.getClientId());
            userFundNavPO.setNavTime(yesDate);
            userFundNavPO.setAccountId(accountUserPO.getAccountId());
            userFundNavPO.setGoalId(accountUserPO.getGoalId());
            UserFundNavPO yesUserFundNav = userFundNavService.selectOneByNavTime(userFundNavPO);
            BigDecimal todayShare = BigDecimal.ZERO;
            if (null == yesUserFundNav) {
                todayShare = rechargeShare.subtract(tncfShares).setScale(6, BigDecimal.ROUND_DOWN);
            } else {
                todayShare = rechargeShare.subtract(tncfShares).add(yesUserFundNav.getTotalShare()).setScale(6, BigDecimal.ROUND_DOWN);
            }

            log.info("todayShare:{},accountId:{},clientId:{},goalId:{},统计用户净值,recahrgeMoney:{},rechargeShare:{},"
                    + "redeemShare:{}, todayNav:{}, yesUserFundNav:{}", todayShare, yesAccountFundNav.getAccountId(),
                    accountUserPO.getClientId(), accountUserPO.getGoalId(), userTpcf, rechargeShare,
                    tncfShares, todayFundNav.getFundNav(), JSON.toJSONString(yesUserFundNav));

            //因为用户的totalasset是份额乘以净值，这个时候有小数舍弃，反算（totalasset除以净值）回不来了。所以有以下逻辑
            //计算用户剩余份额时候，如果剩余份额小于0.0001，全部清空,小数前四位都为0直接置0
            if (todayShare.compareTo(COMPARE_DOT) <= 0) {
                todayShare = BigDecimal.ZERO;
            }

            if (todayShare.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            UserFundNavPO todayUserFundNav = constractUserFundNav(todayShare, todayFundNav.getFundNav(), accountUserPO.getAccountId(),
                    accountUserPO.getClientId(), accountUserPO.getGoalId(), date, todayFundNav.getTotalAsset());
            userFundNavPOList.add(todayUserFundNav);

            //added by wooitatt
            //if (!PropertiesUtil.isProd()) {
                log.info("#Start User Recharge Batch No (handelUserFundShare)");
                insertUserBatchFundNav(todayUserFundNav, rechargeShare);

            //}

        }

        return userFundNavPOList;
    }

    private UserFundNavPO constractUserFundNav(BigDecimal todayShare, BigDecimal todayNav, Long accountId,
            String clientId, String goalId, Date date, BigDecimal accountTotalAsset) {
        //BigDecimal totalAsset = todayNav.multiply(todayShare).setScale(6, BigDecimal.ROUND_DOWN);
        UserFundNavPO userFundNavPO = new UserFundNavPO();
        userFundNavPO.setClientId(clientId);
        userFundNavPO.setFundNav(todayNav);
        //userFundNavPO.setTotalAsset(totalAsset);
        userFundNavPO.setTotalAsset(accountTotalAsset);
        userFundNavPO.setTotalShare(todayShare);
        userFundNavPO.setNavTime(DateUtils.dayStart(date));
        userFundNavPO.setAccountId(accountId);
        userFundNavPO.setCreateTime(DateUtils.now());
        userFundNavPO.setUpdateTime(DateUtils.now());
        userFundNavPO.setId(Sequence.next());
        userFundNavPO.setGoalId(goalId);
        return userFundNavPO;
    }

    private BigDecimal getAccountRedeemMoney(AccountUserPO accountUser, Date startTime, Date endTime) {
        AccountRedeemPO accountRedeemParam = new AccountRedeemPO();
        accountRedeemParam.setAccountId(accountUser.getAccountId());
        accountRedeemParam.setStartRedeemApplyTime(startTime);
        accountRedeemParam.setEndRedeemApplyTime(endTime);
        accountRedeemParam.setGoalId(accountUser.getGoalId());
        accountRedeemParam.setClientId(accountUser.getClientId());
        List<AccountRedeemPO> accountRedeemList = accountRedeemService.getRedeemListByTime(accountRedeemParam);

        BigDecimal totalApplyRedeem = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeem : accountRedeemList) {
            totalApplyRedeem = totalApplyRedeem.add(accountRedeem.getApplyMoney());
        }

        return totalApplyRedeem;
    }

    /**
     * 计算账户的自建基金净值
     *
     * @param accountInfoPO
     * @param date
     * @param etfClosingPriceMap
     */
    private AccountFundNavPO handelAccountFundNav(AccountInfoPO accountInfoPO,
            BigDecimal totalAssetBeforeWithdrawal,
            boolean isFirst,
            AccountFundNavPO yesterdayFundNavPO,
            Date date,
            BigDecimal cashHolding,
            BigDecimal totalEquit,
            Map<String, BigDecimal> etfClosingPriceMap) {
        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        BigDecimal navTpcfMoney = getTcpf(accountInfoPO.getId());
        if (isFirst) {
            /**
             * 1、第一天的自建基金净值计算: 净值就是 1 ,份额就是总资产除以净值. 计算share = rechargeMoney
             * initDay 这里根据需求公式计算不成立即: nav = totalAssetBeforeWithdrawal / share
             * 不成立
             */
            BigDecimal share = navTpcfMoney;
            accountFundNavWrapperBean = handelInitialDay(totalAssetBeforeWithdrawal, accountInfoPO, date, cashHolding, share);
        } else {
            /**
             * 2.非首日 : 计算非首次自建基金净值
             */
            BigDecimal tncf = tpcfTncfService.getAccountTncfMoney(accountInfoPO.getId());
            if (navTpcfMoney.compareTo(BigDecimal.ZERO) == 0 && tncf.compareTo(BigDecimal.ZERO) == 0) {
                //1、正负现金流都为0
                accountFundNavWrapperBean = handelNoCashFlow(totalAssetBeforeWithdrawal, yesterdayFundNavPO, date, cashHolding);
            }
            if (navTpcfMoney.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
                //2、只有正现金流: TPCF
                accountFundNavWrapperBean = handelOnlyRechargeCashFlow(totalAssetBeforeWithdrawal, navTpcfMoney, yesterdayFundNavPO, date, cashHolding, totalEquit);
            }
            if (navTpcfMoney.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
                //3、只有负现金流: TNCF(该值为负值)
                accountFundNavWrapperBean = handelOnlyWithdrawalCashFlow(totalAssetBeforeWithdrawal, accountInfoPO, yesterdayFundNavPO, date, cashHolding, tncf);
            }
            if (navTpcfMoney.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
                //4、既有正现金流和负现金流
                accountFundNavWrapperBean = handelRechargeAndRedeemCashFlow(totalAssetBeforeWithdrawal, navTpcfMoney, yesterdayFundNavPO, date, cashHolding, totalEquit, tncf, accountInfoPO);
            }

        }

        //统计中间数据处理
        handelCalFundNav(accountFundNavWrapperBean, etfClosingPriceMap, date, accountInfoPO);
        return accountFundNavWrapperBean.getAccountFundNavPO();
    }

    /**
     * 统计数据处理
     *
     * @param accountFundNavWrapperBean
     * @param etfClosingPriceMap
     * @param date
     * @param accountInfoPO
     */
    private void handelCalFundNav(AccountFundNavWrapperBean accountFundNavWrapperBean,
            Map<String, BigDecimal> etfClosingPriceMap,
            Date date,
            AccountInfoPO accountInfoPO) {
        //重算totalasset和totalcash
        AccountAssetPO queryParam = new AccountAssetPO();
        queryParam.setAccountId(accountInfoPO.getId());
        queryParam.setCreateEndTime(DateUtils.dayEnd(date));
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
        if (CollectionUtils.isEmpty(accountAssetPOs)) {
            log.info("进行基金净值计算,用户资产统计,该账户没有资产,不做处理。accountId:{}", accountInfoPO.getId());
            throw new BusinessException("用户资产统计,该账户没有资产,不做处理。accountId:" + accountInfoPO.getId());
        }
        //查询该账号上的总资产
        List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
        //过滤出持有中的资产
        BigDecimal totalAssetAfterFund = BigDecimal.ZERO;
        Map<String, AccountAssetStatisticBean> allHoldProductMap = Maps.newHashMap();
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeens) {
            if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING) {
                allHoldProductMap.put(accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean);
                totalAssetAfterFund = totalAssetAfterFund.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
            }
        }
        //账户资产为0 表示就是initDay
        accountFundNavWrapperBean.getAccountFundNavPO().setTotalAsset(totalAssetAfterFund);

        //统计totalCash
        BigDecimal totalCash = BigDecimal.ZERO;
        if (allHoldProductMap.get(Constants.CASH) != null) {
            BigDecimal productMoney = allHoldProductMap.get(Constants.CASH).getProductMoney();
            totalCash = totalCash.add(productMoney);
        }
        if (allHoldProductMap.get(Constants.UN_BUY_PRODUCT_CODE) != null) {
            BigDecimal productMoney = allHoldProductMap.get(Constants.UN_BUY_PRODUCT_CODE).getProductMoney();
            totalCash = totalCash.add(productMoney);
        }
        accountFundNavWrapperBean.getAccountFundNavPO().setTotalCash(totalCash);
        accountFundNavWrapperBean.setAdjCashHolding(totalCash);

        CalFundNavEvent calFundNavEvent = new CalFundNavEvent();
        calFundNavEvent.setAccountId(accountInfoPO.getId());
        calFundNavEvent.setAdjCashHolding(totalCash);
        calFundNavEvent.setAdjFundAsset(totalAssetAfterFund);
        calFundNavEvent.setAdjFundShares(accountFundNavWrapperBean.getAdjFundShares());
        calFundNavEvent.setCashHolding(accountFundNavWrapperBean.getCashHolding());
        calFundNavEvent.setCashWithdraw(accountFundNavWrapperBean.getCashWithdraw());
        calFundNavEvent.setNavInUsd(accountFundNavWrapperBean.getNavInUsd());

        BigDecimal lastTotalAsset = BigDecimal.ZERO;
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeens) {
            if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING
                    && !accountAssetStatisticBean.getProductCode().equals(Constants.CASH)
                    && !accountAssetStatisticBean.getProductCode().equals(Constants.UN_BUY_PRODUCT_CODE)) {
                lastTotalAsset = lastTotalAsset.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
            }
        }
        //记录中间值
        calFundNavEvent.setTotalEquity(lastTotalAsset);
        calFundNavEvent.setTotalFundValue(accountFundNavWrapperBean.getTotalFundValue());
        calFundNavEvent.setFundShares(accountFundNavWrapperBean.getFundShares());
        calFundNavEvent.setDate(date);
        eventBus.post(calFundNavEvent);
    }

    /**
     * 获取TPCF 今日充值 + 分红
     *
     * @param accountId
     * @return
     */
    private BigDecimal getTcpf(Long accountId) {
        List<AccountRechargePO> accountRechargePOS = tpcfTncfService.getAccountTpcfWithAssetComplete(accountId);
        BigDecimal accountTpcf = tpcfTncfService.getAccountTpcfMoney(accountRechargePOS);

        //获取分红
        AccountDividendPO accountDividendParam = new AccountDividendPO();
        accountDividendParam.setAccountId(accountId);
        accountDividendParam.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        BigDecimal accountDividendMoney = accountDividendService.getAccountDividendMoney(accountDividendParam);

        BigDecimal navTpcfMoney = accountTpcf.add(accountDividendMoney).setScale(6, BigDecimal.ROUND_HALF_UP);
        log.info("获取NAV的TPCF：accountId:{},navTpcfMoney:{},accountTpcf:{}", accountId, navTpcfMoney, accountTpcf);
        return navTpcfMoney;
    }

    /**
     * 既有正现金流和负现金流
     * <p>
     * 1、计算当日资产: Fund_Asset(t) = Total Cash + Total Equity 2、当日计算净值 = (当日资产 -
     * 正向资金流) / 昨日份额: (Fund_Asset(t)-TPCF(t)) / ADJ_FundShares(t-1) 3、临时份额 =
     * 昨日份额 + (正像现金流 / 当日净值) : FundShares(t) = ADJ_FundShares(t-1)+TPCF(t) /
     * FundNAV(t) 4、当日份额 = 临时份额 + (负现金流(该值为负数) / 昨日净值); ADJ_FundShares(t)=
     * FundShares(t)+ TNCF(t)/ FundNAV(t-1) 5、当日资产 = cash + 提现资产流(该值为负值) +
     * Current_(EV_USD(t) ) ***(这个值理论和统记的总资产相同)
     *
     * @param totalAsset
     * @param todayRechargeMoney
     * @param yesterdayFundNavPO
     * @param date
     */
    private AccountFundNavWrapperBean handelRechargeAndRedeemCashFlow(BigDecimal totalAsset,
            BigDecimal todayRechargeMoney,
            AccountFundNavPO yesterdayFundNavPO,
            Date date, BigDecimal cashHolding,
            BigDecimal totalEquit,
            BigDecimal tncf,
            AccountInfoPO accountInfoPO) {
        //今日净值
        BigDecimal todayNav = totalAsset.subtract(todayRechargeMoney)
                .divide(yesterdayFundNavPO.getTotalShare(), BigDecimal.ROUND_HALF_UP).setScale(6, BigDecimal.ROUND_DOWN);
        //临时份额
        //FundShares(t) = ADJ_FundShares(t-1)+TPCF(t) / FundNAV(t)
        BigDecimal tmpShare = yesterdayFundNavPO.getTotalShare().add(todayRechargeMoney.divide(todayNav, BigDecimal.ROUND_HALF_DOWN))
                .setScale(4, BigDecimal.ROUND_HALF_UP);

        //获取TNCF
        AccountRedeemPO queryParam = new AccountRedeemPO();
        queryParam.setAccountId(yesterdayFundNavPO.getAccountId());
        queryParam.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
        //queryParam.setTncfStatus(TncfStatusEnum.TNCF);
        queryParam.setTncfStatus(TncfStatusEnum.ASSETSELLCOMPLETE);
        //List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.getRedeemListByTime(queryParam);
        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.getRedeemListByTimeOrderPF(queryParam);//Edit By WooiTatt

        //计算每个account下用户提现比例
        AccountRedeemWrapperBean redeemWrapperBean = disUserWithdraw(accountRedeemPOs, todayNav, date, accountInfoPO, totalAsset);

        BigDecimal todayShare = tmpShare.subtract(redeemWrapperBean.getTotalConfirmShares())
                .setScale(6, BigDecimal.ROUND_DOWN);

        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(yesterdayFundNavPO.getAccountId(), todayNav, todayShare, totalAsset, date);

        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
        accountFundNavWrapperBean.setTotalEquity(totalEquit);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(redeemWrapperBean.getAccountCashWithdrawal());
        accountFundNavWrapperBean.setFundShares(tmpShare);
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());

        return accountFundNavWrapperBean;

    }

    /**
     * 分配最终的提现金额
     *
     * @param accountRedeemPOListList
     * @param todayNav
     */
    private AccountRedeemWrapperBean disUserWithdraw(List<AccountRedeemPO> accountRedeemPOListList,
            BigDecimal todayNav,
            Date date,
            AccountInfoPO accountInfoPO,
            BigDecimal totalAsset) {
        BigDecimal accountTotalConfirmRedeemMoney = BigDecimal.ZERO;
        BigDecimal accountTotalApplyRedeemMoney = BigDecimal.ZERO;
        BigDecimal accountTotalConfirmShares = BigDecimal.ZERO;

        //按用户分组
        Multimap<String, AccountRedeemPO> goalAccountRedeemMap = ArrayListMultimap.create();
        for (AccountRedeemPO accountRedeem : accountRedeemPOListList) {
            String key = accountRedeem.getClientId() + "_" + accountRedeem.getGoalId();
            goalAccountRedeemMap.put(key, accountRedeem);
        }
        Set<String> goalKeySet = goalAccountRedeemMap.keySet();
        for (String goal : goalKeySet) {
            List<AccountRedeemPO> accountRedeemPOList = (List<AccountRedeemPO>) goalAccountRedeemMap.get(goal);
            //将accountredeem分为非全赎和全赎两个列表
            Iterable<AccountRedeemPO> accountRedeemNotAllRedeemList = Iterables.filter(accountRedeemPOList, new Predicate<AccountRedeemPO>() {
                @Override
                public boolean apply(@Nullable AccountRedeemPO input) {
                    return input.getRedeemType() != RedeemTypeEnum.ALLRedeem;
                }
            });
            log.info("部分提现:{}", JSON.toJSONString(accountRedeemNotAllRedeemList));
            BigDecimal goalConfirmSharesNotAll = BigDecimal.ZERO;
            for (AccountRedeemPO accountRedeem : accountRedeemNotAllRedeemList) {
                accountTotalApplyRedeemMoney = accountTotalApplyRedeemMoney.add(accountRedeem.getApplyMoney());
                //Cash_Wihdraw(t) =TNCF(t)/ FundNAV(t-1) * FundNAV(t)
                AccountFundNavPO accountNavQuery = new AccountFundNavPO();
                accountNavQuery.setAccountId(accountRedeem.getAccountId());
                accountNavQuery.setNavTime(accountRedeem.getNavDate());
                AccountFundNavPO redeemFundNav = accountNavService.selectOneByNavTime(accountNavQuery);
                //Edited By WooiTatt (Confirm Share and Confirm Money)
                
                BigDecimal confirmShares = BigDecimal.ZERO;
                BigDecimal confirmMoney = BigDecimal.ZERO;
                if(accountRedeem.getIsAnnualPerformanceFee().equalsIgnoreCase("N")){
                    confirmShares = accountRedeem.getApplyMoney()
                    .divide(redeemFundNav.getFundNav(), 6, BigDecimal.ROUND_DOWN);
                    confirmMoney = handleAccountRedeem(accountRedeem, confirmShares, null, todayNav);
                }else{
                    AccountPerformanceFeeDetails accPerFeeDetails = new AccountPerformanceFeeDetails();
                    accPerFeeDetails = accountPerformanceFeeDetailsService.queryById(accountRedeem.getAccPerformanceFeeDesId());
                    BigDecimal performanceFee = accPerFeeDetails.getPerformanceFee();
                    BigDecimal performanceFeeGst = accPerFeeDetails.getPerformanceFeeGst();
                    BigDecimal sumPerformanceFee = performanceFee.add(performanceFeeGst);
                    confirmShares = sumPerformanceFee.divide(todayNav, 6, BigDecimal.ROUND_DOWN);
                    confirmMoney = handleAccountRedeemAnnualPerfee(accountRedeem, confirmShares, 
                            null, todayNav, performanceFee, performanceFeeGst );
                }
                goalConfirmSharesNotAll = goalConfirmSharesNotAll.add(confirmShares);
                // BigDecimal confirmMoney = handleAccountRedeem(accountRedeem, confirmShares, null, todayNav);
                accountTotalConfirmRedeemMoney = accountTotalConfirmRedeemMoney.add(confirmMoney);
                accountTotalConfirmShares = accountTotalConfirmShares.add(confirmShares);
            }

            Iterable<AccountRedeemPO> accountRedeemAllRedeemList = Iterables.filter(accountRedeemPOList, new Predicate<AccountRedeemPO>() {
                @Override
                public boolean apply(@Nullable AccountRedeemPO input) {
                    return input.getRedeemType() == RedeemTypeEnum.ALLRedeem;
                }
            });
            log.info("全部提现:{}", JSON.toJSONString(accountRedeemAllRedeemList));
            for (AccountRedeemPO accountRedeem : accountRedeemAllRedeemList) {
                accountTotalApplyRedeemMoney = accountTotalApplyRedeemMoney.add(accountRedeem.getApplyMoney());
                Date yesDate = DateUtils.addDays(date, -1);
                UserFundNavPO userFundNavPO = new UserFundNavPO();
                userFundNavPO.setClientId(accountRedeem.getClientId());
                userFundNavPO.setNavTime(yesDate);
                userFundNavPO.setAccountId(accountRedeem.getAccountId());
                userFundNavPO.setGoalId(accountRedeem.getGoalId());
                UserFundNavPO yesUserFundNav = userFundNavService.selectOneByNavTime(userFundNavPO);
                BigDecimal confirmShares = yesUserFundNav.getTotalShare().subtract(goalConfirmSharesNotAll);
//                BigDecimal confirmMoney = yesUserFundNav.getTotalAsset().subtract(accountTotalConfirmRedeemMoney);

                BigDecimal confirmMoney = BigDecimal.ZERO;
                //如果是tailor,全部从计算nav中资产出钱
                if (accountInfoPO.getInvestType() == AccountTypeEnum.TAILOR) {
                    totalAsset = totalAsset.subtract(accountTotalConfirmRedeemMoney);
                    confirmMoney = handleAccountRedeem(accountRedeem, confirmShares, totalAsset, todayNav);
                } else {
                    confirmMoney = handleAccountRedeem(accountRedeem, confirmShares, null, todayNav);
                }
                accountTotalConfirmRedeemMoney = accountTotalConfirmRedeemMoney.add(confirmMoney);
                accountTotalConfirmShares = accountTotalConfirmShares.add(confirmShares);
            }
        }

//        //更新accountRedeem
//        for (AccountRedeemPO accountRedeem : accountRedeemPOListList) {
//            accountTotalApplyRedeemMoney = accountTotalApplyRedeemMoney.add(accountRedeem.getApplyMoney());
//            //Cash_Wihdraw(t) =TNCF(t)/ FundNAV(t-1) * FundNAV(t)
//            AccountFundNavPO accountNavQuery = new AccountFundNavPO();
//            accountNavQuery.setAccountId(accountRedeem.getAccountId());
//            accountNavQuery.setNavTime(accountRedeem.getNavDate());
//            AccountFundNavPO redeemFundNav = accountNavService.selectOneByNavTime(accountNavQuery);
//            BigDecimal confirmShares = BigDecimal.ZERO;
//            if(accountRedeem.getRedeemType() == RedeemTypeEnum.ALLRedeem){
//                Date yesDate = DateUtils.addDays(date, -1);
//                UserFundNavPO userFundNavPO = new UserFundNavPO();
//                userFundNavPO.setClientId(accountRedeem.getClientId());
//                userFundNavPO.setNavTime(yesDate);
//                userFundNavPO.setAccountId(accountRedeem.getAccountId());
//                userFundNavPO.setGoalId(accountRedeem.getGoalId());
//                UserFundNavPO yesUserFundNav = userFundNavService.selectOneByNavTime(userFundNavPO);
//                confirmShares = yesUserFundNav.getTotalShare();
//            }else{
//                confirmShares = accountRedeem.getApplyMoney().divide(redeemFundNav.getFundNav(), 6, BigDecimal.ROUND_DOWN);
//            }
//
//            BigDecimal confirmMoney = confirmShares.multiply(todayNav).setScale(6, BigDecimal.ROUND_DOWN);
//
//            accountRedeem.setConfirmMoney(confirmMoney);
//            accountRedeem.setConfirmShares(confirmShares);
//            accountRedeem.setRedeemConfirmTime(DateUtils.now());
//            accountRedeemService.updateOrInsert(accountRedeem);
//
//            RedeemApplyPO redeemApplyPO = redeemApplyService.queryById(accountRedeem.getRedeemApplyId());
//            redeemApplyPO.setConfirmAmount(confirmMoney);
//            redeemApplyService.updateOrInsert(redeemApplyPO);
//
//            accountTotalConfirmRedeemMoney = accountTotalConfirmRedeemMoney.add(confirmMoney);
//
//            //新增cash资产流水
//            AccountAssetPO cashAsset = new AccountAssetPO();
//            cashAsset.setAssetSource(AssetSourceEnum.CASHWITHDRAWAL);
//            cashAsset.setAccountId(accountRedeem.getAccountId());
//            cashAsset.setConfirmTime(DateUtils.now());
//            cashAsset.setConfirmShare(BigDecimal.ZERO);
//            cashAsset.setApplyMoney(confirmMoney);
//            cashAsset.setConfirmMoney(confirmMoney);
//            cashAsset.setApplyTime(DateUtils.now());
//            cashAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
//            cashAsset.setProductCode(Constants.CASH);
//            cashAsset.setCreateTime(accountRedeem.getCreateTime());
//            accountAssetService.updateOrInsert(cashAsset);
//        }
//        Date yesDate = DateUtils.addDays(date, -1);
//        Date endTime = DateUtils.getDate(yesDate, 10, 0, 0);
//        Date startTime = DateUtils.addDays(endTime, -1);
//        RedeemApplyPO vaRedeemApplyPO = new RedeemApplyPO();
//        vaRedeemApplyPO.setAccountId(yesterdayFundNavPO.getAccountId());
//        vaRedeemApplyPO.setStartApplyTime(startTime);
//        vaRedeemApplyPO.setEndApplyTime(endTime);
//        vaRedeemApplyPO.setWithdrawalSourceType(WithdrawalSourceTypeEnum.FROMGOAL);
//        List<RedeemApplyPO> redeemApplyPOList = redeemApplyService.queryByApplyTime(vaRedeemApplyPO);
//        if (CollectionUtils.isEmpty(redeemApplyPOList)) {
//            log.info("{}该账号没有提现申请记录", yesterdayFundNavPO.getAccountId());
//        }
//        for (RedeemApplyPO redeemApplyPO : redeemApplyPOList) {
//            BigDecimal clientApplyRedeem = redeemApplyPO.getApplyMoney();
//            BigDecimal clientApplyPrecent = clientApplyRedeem.divide(totalApplyRedeem, 6, BigDecimal.ROUND_HALF_DOWN);
//            BigDecimal confirmMoney = accountTotalConfirmRedeemMoney.multiply(clientApplyPrecent);
//            redeemApplyPO.setConfirmAmount(confirmMoney);
//            redeemApplyService.updateOrInsert(redeemApplyPO);
//        }
        AccountRedeemWrapperBean accountRedeemWrapperBean = new AccountRedeemWrapperBean();
        accountRedeemWrapperBean.setAccountCashWithdrawal(accountTotalConfirmRedeemMoney.setScale(6, BigDecimal.ROUND_HALF_UP));
        accountRedeemWrapperBean.setTotalApplyRedeem(accountTotalApplyRedeemMoney.setScale(6, BigDecimal.ROUND_HALF_UP));
        accountRedeemWrapperBean.setTotalConfirmShares(accountTotalConfirmShares);
        return accountRedeemWrapperBean;
    }

    private BigDecimal handleAccountRedeem(AccountRedeemPO accountRedeem,
            BigDecimal confirmShares,
            BigDecimal tailorAllRedeemMoney,
            BigDecimal todayNav) {
        BigDecimal confirmMoney = BigDecimal.ZERO;
        if (tailorAllRedeemMoney != null) {
            confirmMoney = tailorAllRedeemMoney;
        } else {
            confirmMoney = confirmShares.multiply(todayNav).setScale(6, BigDecimal.ROUND_DOWN);
        }

        //Added By WooiTatt (Performance Fee withdraw)
        BigDecimal totalPerformanceFee = BigDecimal.ZERO;
        BigDecimal sumPerformanceFee = BigDecimal.ZERO;
        BigDecimal sumPerformanceFeeGst = BigDecimal.ZERO;
        
        //String strToday = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT);
        String strToday = DateUtils.formatDate(accountRedeem.getRedeemApplyTime(), DateUtils.DATE_FORMAT); 
        String strStartDate = strStartDatePF;
        Date today = DateUtils.parseDate(strToday);
        Date startDate = DateUtils.parseDate(strStartDate);
        boolean isStartPerformanceFee = false;
        
        //if(today.compareTo(startDate)==0 ||today.compareTo(startDate)==1 ){
        //    isStartPerformanceFee = true;
        //}
        
        //if (!PropertiesUtil.isProd()) {
            UserBatchNavPO usrBatchNav = new UserBatchNavPO();
            usrBatchNav.setClientId(accountRedeem.getClientId());
            usrBatchNav.setGoalId(accountRedeem.getGoalId());
            usrBatchNav.setStatus(UserBatchNavEnum.ACTIVE);
            log.info("#Start Calculate Performance Fee.clientID:{}, goalID:{} ", accountRedeem.getClientId(), accountRedeem.getGoalId());
            List<UserBatchNavPO> lUsrBatchNav = userBatchNavService.listUserBatchNavWithActive(usrBatchNav);

            //BigDecimal calConfirmShare = confirmShares;
            BigDecimal withdrawShare = confirmShares;
            //BigDecimal balanceCal = BigDecimal.ZERO;
            for (UserBatchNavPO usrBatchPO : lUsrBatchNav) {
                
                AccountPerformanceFeeDetails accPerformanceDetailPO = new AccountPerformanceFeeDetails();
                accPerformanceDetailPO.setClientId(usrBatchPO.getClientId());
                accPerformanceDetailPO.setGoalId(usrBatchPO.getGoalId());
                accPerformanceDetailPO.setFeeType(PerformanceFeeTypeEnum.WITHDRAW);
                accPerformanceDetailPO.setUserBatchId(usrBatchPO.getId().toString());
                accPerformanceDetailPO.setCreateTime(DateUtils.now());
                List<AccountPerformanceFeeDetails> listAccPFDetails = 
                        accountPerformanceFeeDetailsService.getListAccPerformanceFeeDetails(accPerformanceDetailPO);
                BigDecimal deductedShare = BigDecimal.ZERO;
                for(AccountPerformanceFeeDetails accPFDetailsPO: listAccPFDetails){
                    deductedShare.add(accPFDetailsPO.getShare());
                }
                BigDecimal todayShare = usrBatchPO.getCurrTotalShare().add(deductedShare);
                //BigDecimal shareBalance = usrBatchPO.getCurrTotalShare().subtract(calConfirmShare);
                BigDecimal shareBalance = usrBatchPO.getCurrTotalShare().subtract(withdrawShare);
                
                BigDecimal diffNav = todayNav.subtract(usrBatchPO.getCurrFundNav());
                
                boolean isNegBalance = false;
                UserBatchNavPO usrBatchNavPO = new UserBatchNavPO();
                AccountPerformanceFeeDetails accountPerformanceFeeDetails = new AccountPerformanceFeeDetails();
                
               if(shareBalance.compareTo(new BigDecimal("0"))== -1){
                    isNegBalance = true;
                    withdrawShare = usrBatchPO.getCurrTotalShare();
                }
               
                if(isStartPerformanceFee){
                    if (diffNav.compareTo(new BigDecimal("0")) == 1) {
                       BigDecimal performanceFeeAmount = todayShare.multiply(diffNav).multiply(new BigDecimal("0.1")).setScale(6, BigDecimal.ROUND_DOWN);
                       BigDecimal performanceFee = performanceFeeAmount.multiply(withdrawShare).divide(todayShare,6,BigDecimal.ROUND_DOWN);

                       BigDecimal performanceFeeGst = performanceFee.multiply(Constants.GST_RATE_PRICE).setScale(6, BigDecimal.ROUND_DOWN);
                       totalPerformanceFee = totalPerformanceFee.add(performanceFee.add(performanceFeeGst)).setScale(6, BigDecimal.ROUND_DOWN);
                       sumPerformanceFee = sumPerformanceFee.add(performanceFee).setScale(6, BigDecimal.ROUND_DOWN);
                       sumPerformanceFeeGst = sumPerformanceFeeGst.add(performanceFeeGst).setScale(6, BigDecimal.ROUND_DOWN);

                       accountPerformanceFeeDetails.setPerformanceFee(performanceFee);
                       accountPerformanceFeeDetails.setPerformanceFeeGst(performanceFeeGst);
                       accountPerformanceFeeDetails.setShare(withdrawShare);
                       accountPerformanceFeeDetails.setAccountId(usrBatchPO.getAccountId());
                       accountPerformanceFeeDetails.setClientId(usrBatchPO.getClientId());
                       accountPerformanceFeeDetails.setGoalId(usrBatchPO.getGoalId());
                       accountPerformanceFeeDetails.setDayNav(todayNav);
                       accountPerformanceFeeDetails.setDepositNav(usrBatchPO.getCurrFundNav());
                       accountPerformanceFeeDetails.setFeeType(PerformanceFeeTypeEnum.WITHDRAW);
                       accountPerformanceFeeDetails.setUserBatchId(usrBatchPO.getId().toString());

                    }else{
                       accountPerformanceFeeDetails.setPerformanceFee(new BigDecimal("0"));
                       accountPerformanceFeeDetails.setPerformanceFeeGst(new BigDecimal("0"));
                       accountPerformanceFeeDetails.setShare(withdrawShare);
                       accountPerformanceFeeDetails.setAccountId(usrBatchPO.getAccountId());
                       accountPerformanceFeeDetails.setClientId(usrBatchPO.getClientId());
                       accountPerformanceFeeDetails.setGoalId(usrBatchPO.getGoalId());
                       accountPerformanceFeeDetails.setDayNav(todayNav);
                       accountPerformanceFeeDetails.setDepositNav(usrBatchPO.getCurrFundNav());
                       accountPerformanceFeeDetails.setFeeType(PerformanceFeeTypeEnum.WITHDRAW);
                       accountPerformanceFeeDetails.setUserBatchId(usrBatchPO.getId().toString());
                    }
                    accountPerformanceFeeDetailsService.saveAccountPerformanceFeeDetails(accountPerformanceFeeDetails);
                }
                
                if(isNegBalance){
                    withdrawShare = shareBalance.abs();
                    shareBalance = BigDecimal.ZERO;
                }
                
                usrBatchNavPO.setId(usrBatchPO.getId());
                usrBatchNavPO.setCurrTotalShare(shareBalance);
                if (shareBalance.compareTo(new BigDecimal("0")) == 0) {
                     usrBatchNavPO.setStatus(UserBatchNavEnum.DEACTIVE);
                }
                userBatchNavService.updateTotalShare(usrBatchNavPO);
                
                if(!isNegBalance){
                    break;
                }
                
            }
            if(isStartPerformanceFee){
                if(totalPerformanceFee.compareTo(new BigDecimal("0")) == 1){
                    AccountPerformanceFee accountPerformanceFee = new AccountPerformanceFee();
                    accountPerformanceFee.setAccountId(accountRedeem.getAccountId().toString());
                    accountPerformanceFee.setClientId(accountRedeem.getClientId());
                    accountPerformanceFee.setGoalId(accountRedeem.getGoalId());
                    accountPerformanceFee.setFeeType(PerformanceFeeTypeEnum.WITHDRAW);
                    accountPerformanceFee.setPerformanceFee(sumPerformanceFee);
                    accountPerformanceFee.setPerformanceFeeGst(sumPerformanceFeeGst);
                    accountPerformanceFee.setPerformanceTotalFee(totalPerformanceFee);
                    accountPerformanceFee.setStatus(PerformanceFeeStatusEnum.PROCESSING);

                    accountPerformanceFeeService.saveAccountPerformanceFee(accountPerformanceFee);
                    log.info("#Finished Calculate Performance Fee.clientID:{}, goalID:{} ", accountRedeem.getClientId(), accountRedeem.getGoalId());

                    List<PivotFeeDetailPO> pivotFeeDetailDTOList = Lists.newArrayList();

                    PivotFeeDetailPO performFee = new PivotFeeDetailPO();
                    performFee.setAccountId(accountRedeem.getAccountId());
                    performFee.setClientId(Long.valueOf(accountRedeem.getClientId()));
                    performFee.setGoalId(accountRedeem.getGoalId());
                    performFee.setFeeType(FeeTypeEnum.PERFORMANCE_FEE);
                    performFee.setOperateType(OperateTypeEnum.RECHARGE);
                    performFee.setOperateDate(DateUtils.dayStart(DateUtils.now()));
                    performFee.setMoney(sumPerformanceFee);
                    pivotFeeDetailDTOList.add(performFee);

                    PivotFeeDetailPO performFeeGst = new PivotFeeDetailPO();
                    performFeeGst.setAccountId(accountRedeem.getAccountId());
                    performFeeGst.setClientId(Long.valueOf(accountRedeem.getClientId()));
                    performFeeGst.setGoalId(accountRedeem.getGoalId());
                    performFeeGst.setFeeType(FeeTypeEnum.PERFORMANCE_GST);
                    performFeeGst.setOperateType(OperateTypeEnum.RECHARGE);
                    performFeeGst.setOperateDate(DateUtils.dayStart(DateUtils.now()));
                    performFeeGst.setMoney(sumPerformanceFeeGst);
                    pivotFeeDetailDTOList.add(performFeeGst);

                    pivotFeeDetailService.batchInsert(pivotFeeDetailDTOList);
                 }
            }
        //}

        if(isStartPerformanceFee){
             accountRedeem.setConfirmMoney(confirmMoney.subtract(totalPerformanceFee)); //Edit WooiTatt
        }else{
            accountRedeem.setConfirmMoney(confirmMoney);
        }
        accountRedeem.setConfirmShares(confirmShares);
        accountRedeem.setRedeemConfirmTime(DateUtils.now());
        accountRedeemService.updateOrInsert(accountRedeem);

        RedeemApplyPO redeemApplyPO = redeemApplyService.queryById(accountRedeem.getRedeemApplyId());
        if(isStartPerformanceFee){
            redeemApplyPO.setConfirmAmount(confirmMoney.subtract(totalPerformanceFee)); //Edit WooiTatt
        }else{
            redeemApplyPO.setConfirmAmount(confirmMoney);
        }
       
        redeemApplyService.updateOrInsert(redeemApplyPO);

//        accountTotalConfirmRedeemMoney = accountTotalConfirmRedeemMoney.add(confirmMoney);
        //新增cash资产流水
        AccountAssetPO cashAsset = new AccountAssetPO();
        cashAsset.setAssetSource(AssetSourceEnum.CASHWITHDRAWAL);
        cashAsset.setAccountId(accountRedeem.getAccountId());
        cashAsset.setConfirmTime(DateUtils.now());
        cashAsset.setConfirmShare(BigDecimal.ZERO);
        cashAsset.setApplyMoney(confirmMoney);
        if(isStartPerformanceFee){
            cashAsset.setConfirmMoney(confirmMoney.subtract(totalPerformanceFee)); //Edit By WooiTatt
        }else{
            cashAsset.setConfirmMoney(confirmMoney);
        }
        cashAsset.setApplyTime(DateUtils.now());
        cashAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
        cashAsset.setProductCode(Constants.CASH);
        cashAsset.setCreateTime(accountRedeem.getCreateTime());
        accountAssetService.updateOrInsert(cashAsset);
        
        //Added By WooiTatt
        if(isStartPerformanceFee){
            if(totalPerformanceFee.compareTo(new BigDecimal("0")) == 1){
                cashAsset = new AccountAssetPO();
                cashAsset.setAssetSource(AssetSourceEnum.NORMALFEE); //Record Charge Performance Fee
                cashAsset.setAccountId(accountRedeem.getAccountId());
                cashAsset.setConfirmTime(DateUtils.now());
                cashAsset.setConfirmShare(BigDecimal.ZERO);
                cashAsset.setApplyMoney(totalPerformanceFee);
                cashAsset.setConfirmMoney(totalPerformanceFee);
                cashAsset.setApplyTime(DateUtils.now());
                cashAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
                cashAsset.setProductCode(Constants.CASH);
                cashAsset.setCreateTime(accountRedeem.getCreateTime());
                accountAssetService.updateOrInsert(cashAsset);
            }
        }
        log.info("#End Calculate Performance Fee.clientID:{}, goalID:{} ", accountRedeem.getClientId(), accountRedeem.getGoalId());
        return confirmMoney;
    }

    /**
     * 只有正向现金流
     * <p>
     * 1、计算当日资产: Fund_Asset(t) = Total Cash + Total Equity 2、计算当日净值 = (总资产 -
     * 今日cash) / 昨日份额; FundNAV(t)= (Fund_Asset(t)-TPCF(t)) / ADJ_FundShares(t-1)
     * 3、计算当日份额 = 昨日资产 + (正向资产 / 当日净值); FundShares(t) =
     * ADJ_FundShares(t-1)+TPCF(t) / FundNAV(t)
     *
     * @param totalAssetBeforeWithdrawal
     * @param rechargeAmount
     * @param yesterdayFundNavPO
     * @param date
     */
    private AccountFundNavWrapperBean handelOnlyRechargeCashFlow(BigDecimal totalAssetBeforeWithdrawal, BigDecimal rechargeAmount,
            AccountFundNavPO yesterdayFundNavPO, Date date,
            BigDecimal cashHolding,
            BigDecimal totalEquit) {

        BigDecimal todayNav = totalAssetBeforeWithdrawal.subtract(rechargeAmount)
                .divide(yesterdayFundNavPO.getTotalShare(), 6, BigDecimal.ROUND_DOWN);
        BigDecimal todayShare = yesterdayFundNavPO.getTotalShare()
                .add(rechargeAmount.divide(todayNav, 6, BigDecimal.ROUND_DOWN));

        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(yesterdayFundNavPO.getAccountId(), todayNav, todayShare, totalAssetBeforeWithdrawal, date);

        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
        accountFundNavWrapperBean.setTotalEquity(totalEquit);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(BigDecimal.ZERO);
        accountFundNavWrapperBean.setFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());

        return accountFundNavWrapperBean;
    }

    /**
     * 3、只有负现金流: TNCF(该值为负值) ==3.1、临时资产: Fund_Asset(t) = Total Cash + Total
     * Equity ==3.2、临时份额: FundShares(t) = ADJ_FundShares(t-1) ==3.2、当日净值:
     * FundNAV(t)= Fund_Asset(t) / FundShares(t) ==3.3、当日份额: ADJ_FundShares(t)=
     * FundShares(t)+ TNCF(t)/ FundNAV(t-1) ==3.4、当日现金: ADJ_Cash_SAXO_USD(t)=
     * Cash_SAXO_USD(t) + Cash_Wihdraw(t) ==3.5、当日资产: ADJ_Fund_Asset(t) =
     * ADJ_Cash_SAXO_USD(t) + Current_(EV_USD(t) )
     */
    private AccountFundNavWrapperBean handelOnlyWithdrawalCashFlow(
            BigDecimal totalAsset,
            AccountInfoPO accountInfoPO,
            AccountFundNavPO yesterdayFundNavPO,
            Date date, BigDecimal cashHolding,
            BigDecimal tncf) {
        //获取TNCF
        Date yesDate = DateUtils.addDays(date, -1);
        //查询所有提现
        AccountRedeemPO AccountRedeemParam = new AccountRedeemPO();
        AccountRedeemParam.setAccountId(accountInfoPO.getId());
        AccountRedeemParam.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
        //AccountRedeemParam.setTncfStatus(TncfStatusEnum.TNCF);
        AccountRedeemParam.setTncfStatus(TncfStatusEnum.ASSETSELLCOMPLETE);
        //List<AccountRedeemPO> accountRedeemList = accountRedeemService.getRedeemListByTime(AccountRedeemParam);
        List<AccountRedeemPO> accountRedeemList = accountRedeemService.getRedeemListByTimeOrderPF(AccountRedeemParam); //Edit By WooiTatt

        BigDecimal totalApplyRedeem = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeem : accountRedeemList) {
            totalApplyRedeem = totalApplyRedeem.add(accountRedeem.getApplyMoney());
        }

        //FundNAV(t)= Fund_Asset(t) / FundShares(t)
        BigDecimal todayNav = totalAsset.divide(yesterdayFundNavPO.getTotalShare(), 6, BigDecimal.ROUND_DOWN);
        //todayNav = new BigDecimal("1.0212"); //Edit Testing
        //计算account下用户提现比例
        //按账户汇总提现记录
        AccountRedeemWrapperBean redeemWrapperBean = disUserWithdraw(accountRedeemList, todayNav, date, accountInfoPO, totalAsset);

        BigDecimal todayShare = null;
        log.info("计算自建基金nav:{},{},{}", yesterdayFundNavPO.getTotalShare(), totalApplyRedeem, yesterdayFundNavPO.getFundNav());
        //是tailor的话通过accountredeem的确认份额进行计算
        todayShare = yesterdayFundNavPO.getTotalShare().subtract(redeemWrapperBean.getTotalConfirmShares()).setScale(6, BigDecimal.ROUND_DOWN);

        //只有tncf需要判断是否全赎
        if (yesterdayFundNavPO.getTotalAsset().setScale(2, BigDecimal.ROUND_DOWN).equals(totalApplyRedeem.setScale(2, BigDecimal.ROUND_DOWN))) {
            todayShare = BigDecimal.ZERO;
        }

        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(yesterdayFundNavPO.getAccountId(), todayNav, todayShare, totalAsset, date);

        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
//        accountFundNavWrapperBean.setTotalEquity(totalFundValue);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(redeemWrapperBean.getAccountCashWithdrawal());
        accountFundNavWrapperBean.setFundShares(yesterdayFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());

        return accountFundNavWrapperBean;
    }

    /**
     * 无现金流:1、正负现金流都为0
     * <p>
     * 1.计算当日总资产 : Fund_Asset(t) = Total Cash + Total Equity 2.当日份额即是昨日份额 :
     * FundShares(t) = ADJ_FundShares(t-1) 3.当日进制按照公式计算 : FundNAV(t)=
     * Fund_Asset(t) / FundShares(t)
     *
     * @param totalAssetBeforeWithdrawal
     * @param yesterdayFundNavPO
     * @param date
     */
    private AccountFundNavWrapperBean handelNoCashFlow(BigDecimal totalAssetBeforeWithdrawal, AccountFundNavPO yesterdayFundNavPO, Date date, BigDecimal cashHolding) {
        BigDecimal yesterdayShare = yesterdayFundNavPO.getTotalShare();
        BigDecimal todayNav = totalAssetBeforeWithdrawal.divide(yesterdayShare, 6, BigDecimal.ROUND_DOWN);
        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(yesterdayFundNavPO.getAccountId(), todayNav, yesterdayShare, totalAssetBeforeWithdrawal, date);

        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
//        accountFundNavWrapperBean.setTotalEquity(totalFundValue);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(BigDecimal.ZERO);
        accountFundNavWrapperBean.setFundShares(yesterdayFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());
        return accountFundNavWrapperBean;
    }

    /**
     * initial day 计算基金净值
     *
     * @param totalAssetBeforeWithdrawal
     * @param accountInfoPO
     * @param date
     */
    private AccountFundNavWrapperBean handelInitialDay(BigDecimal totalAssetBeforeWithdrawal,
            AccountInfoPO accountInfoPO,
            Date date,
            BigDecimal cashHolding,
            BigDecimal share) {
        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        BigDecimal todayNav = Constants.INIT_FUND_NAV;

        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(accountInfoPO.getId(), todayNav, share, totalAssetBeforeWithdrawal, date);

        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
//        accountFundNavWrapperBean.setTotalEquity(totalFundValue);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(BigDecimal.ZERO);
        accountFundNavWrapperBean.setFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());
        return accountFundNavWrapperBean;
    }

    private BigDecimal getAccountRecharge(List<AccountAssetPO> accountAssetPOs) {
        BigDecimal confirmMoney = BigDecimal.ZERO;
        for (AccountAssetPO accountAssetPO : accountAssetPOs) {
            if (accountAssetPO.getConfirmMoney().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            confirmMoney = confirmMoney.add(accountAssetPO.getConfirmMoney()).setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        return confirmMoney;
    }

    private AccountFundNavPO constractTodayAssetFundNav(Long accountId, BigDecimal nav, BigDecimal totalShare, BigDecimal totalAssetBeforeWithdrawal, Date date) {
        AccountFundNavPO todayFundNav = new AccountFundNavPO();
        todayFundNav.setNavTime(DateUtils.dayStart(date));
        todayFundNav.setAccountId(accountId);
        todayFundNav.setTotalShare(totalShare);
        todayFundNav.setFundNav(nav);
        todayFundNav.setCreateTime(DateUtils.now());
        todayFundNav.setUpdateTime(DateUtils.now());
        todayFundNav.setTotalAsset(totalAssetBeforeWithdrawal);
        todayFundNav.setId(Sequence.next());
        return todayFundNav;
    }

     //Added By WooiTatt
    private void insertUserBatchFundNav(UserFundNavPO todayUserFundNav, BigDecimal rechargeShare){
                        
        if (rechargeShare.compareTo(new BigDecimal("0")) == 1) {
            UserBatchNavPO usrBatchNavPO = new UserBatchNavPO();
            usrBatchNavPO.setAccountId(todayUserFundNav.getAccountId().toString());
            usrBatchNavPO.setClientId(todayUserFundNav.getClientId());
            usrBatchNavPO.setGoalId(todayUserFundNav.getGoalId());
            log.info("#Query to get USER LAST BATCH NAV.clientID:{}, goalID:{} ", todayUserFundNav.getClientId(), todayUserFundNav.getGoalId());
            UserBatchNavPO usrBatchNav = userBatchNavService.queryLastBatchGoal(usrBatchNavPO);

            usrBatchNavPO = new UserBatchNavPO();
            usrBatchNavPO.setAccountId(todayUserFundNav.getAccountId().toString());
            usrBatchNavPO.setClientId(todayUserFundNav.getClientId());
            usrBatchNavPO.setGoalId(todayUserFundNav.getGoalId());

            if (usrBatchNav != null) {
                usrBatchNavPO.setBatchNo(new BigDecimal(usrBatchNav.getBatchNo().toString()).add(new BigDecimal("1")).toString());
            } else {
                usrBatchNavPO.setBatchNo("1");
            }

            usrBatchNavPO.setCurrTotalShare(rechargeShare);
            usrBatchNavPO.setCurrFundNav(todayUserFundNav.getFundNav());
            usrBatchNavPO.setStatus(UserBatchNavEnum.ACTIVE);

            userBatchNavService.updateOrInsert(usrBatchNavPO);
            log.info("#Insert NEW user batch nav.clientID:{}, goalID:{} ", todayUserFundNav.getClientId(), todayUserFundNav.getGoalId());

        }
    }
    
    //Added By WooiTatt - Handle Account Redeem Annual Performance Fee
    private BigDecimal handleAccountRedeemAnnualPerfee(AccountRedeemPO accountRedeem,
        BigDecimal confirmShares,
        BigDecimal tailorAllRedeemMoney,
        BigDecimal todayNav,
        BigDecimal performanceFee,
        BigDecimal performanceFeeGst) {
    
        BigDecimal confirmMoney = BigDecimal.ZERO;
        BigDecimal fundShare = BigDecimal.ZERO;
        log.info("#handleAccountRedeemAnnualPerfee Started.clientID:{}, goalID:{} ", accountRedeem.getClientId(), accountRedeem.getGoalId());
       // confirmMoney = accountRedeem.getApplyMoney().divide(new BigDecimal("1.1"));
       // performanceFeeGst = confirmMoney.multiply(Constants.GST_RATE_PRICE);
       // performanceFee = confirmMoney.subtract(performanceFeeGst);

        confirmMoney = performanceFee.add(performanceFeeGst).setScale(6, BigDecimal.ROUND_DOWN);
        
        //Update User Nav Batch Fund Share
        UserBatchNavPO usrBatchNav = new UserBatchNavPO();
        usrBatchNav.setId(accountRedeem.getNavBatchId());
        
        UserBatchNavPO userBatchNav = userBatchNavService.queryUserBatch(usrBatchNav);
        fundShare = userBatchNav.getCurrTotalShare().subtract(confirmShares);
        
        
        UserBatchNavPO userBatchNavPo = new UserBatchNavPO();
        userBatchNavPo.setCurrTotalShare(fundShare);
        if(fundShare.compareTo(new BigDecimal("0"))== -1 || fundShare.compareTo(new BigDecimal("0"))== 0){
            userBatchNavPo.setStatus(UserBatchNavEnum.DEACTIVE);
        }
        userBatchNavPo.setId(accountRedeem.getNavBatchId());
        userBatchNavService.updateTotalShare(userBatchNavPo);
        
        //Record performance fee and performance fee details.
        AccountPerformanceFeeDetails accountPerformanceFeeDetails = new AccountPerformanceFeeDetails();
        accountPerformanceFeeDetails.setAccountId(userBatchNav.getAccountId());
        accountPerformanceFeeDetails.setClientId(userBatchNav.getClientId());
        accountPerformanceFeeDetails.setFeeType(PerformanceFeeTypeEnum.ANNUAL);
        accountPerformanceFeeDetails.setGoalId(userBatchNav.getGoalId());
        accountPerformanceFeeDetails.setUserBatchId(userBatchNav.getId().toString());
        accountPerformanceFeeDetails.setDayNav(todayNav);
        //accountPerformanceFeeDetails.setDepositNav(todayNav);
        accountPerformanceFeeDetails.setPerformanceFee(performanceFee);
        accountPerformanceFeeDetails.setPerformanceFeeGst(performanceFeeGst);
        accountPerformanceFeeDetails.setShare(confirmShares);
        accountPerformanceFeeDetailsService.saveAccountPerformanceFeeDetails(accountPerformanceFeeDetails);
        
        AccountPerformanceFee accountPerformanceFee = new AccountPerformanceFee();
        accountPerformanceFee.setAccountId(userBatchNav.getAccountId().toString());
        accountPerformanceFee.setClientId(userBatchNav.getClientId());
        accountPerformanceFee.setGoalId(userBatchNav.getGoalId());
        accountPerformanceFee.setFeeType(PerformanceFeeTypeEnum.ANNUAL);
        accountPerformanceFee.setPerformanceFee(performanceFee);
        accountPerformanceFee.setPerformanceFeeGst(performanceFeeGst);
        accountPerformanceFee.setPerformanceTotalFee(confirmMoney);
        accountPerformanceFee.setStatus(PerformanceFeeStatusEnum.PROCESSING);

        accountPerformanceFeeService.saveAccountPerformanceFee(accountPerformanceFee);
        log.info("#Finished Calculate Performance Fee.clientID:{}, goalID:{} ", accountRedeem.getClientId(), accountRedeem.getGoalId());
        List<PivotFeeDetailPO> pivotFeeDetailDTOList = Lists.newArrayList();

        PivotFeeDetailPO performFee = new PivotFeeDetailPO();
        performFee.setAccountId(accountRedeem.getAccountId());
        performFee.setClientId(Long.valueOf(accountRedeem.getClientId()));
        performFee.setGoalId(accountRedeem.getGoalId());
        performFee.setFeeType(FeeTypeEnum.PERFORMANCE_FEE);
        performFee.setOperateType(OperateTypeEnum.RECHARGE);
        performFee.setOperateDate(DateUtils.dayStart(DateUtils.now()));
        performFee.setMoney(performanceFee);
        pivotFeeDetailDTOList.add(performFee);

        PivotFeeDetailPO performFeeGst = new PivotFeeDetailPO();
        performFeeGst.setAccountId(accountRedeem.getAccountId());
        performFeeGst.setClientId(Long.valueOf(accountRedeem.getClientId()));
        performFeeGst.setGoalId(accountRedeem.getGoalId());
        performFeeGst.setFeeType(FeeTypeEnum.PERFORMANCE_GST);
        performFeeGst.setOperateType(OperateTypeEnum.RECHARGE);
        performFeeGst.setOperateDate(DateUtils.dayStart(DateUtils.now()));
        performFeeGst.setMoney(performanceFeeGst);
        pivotFeeDetailDTOList.add(performFeeGst);

        pivotFeeDetailService.batchInsert(pivotFeeDetailDTOList);
        
        accountRedeem.setConfirmMoney(confirmMoney); 
        accountRedeem.setConfirmShares(confirmShares);
        accountRedeem.setRedeemConfirmTime(DateUtils.now());
        accountRedeemService.updateOrInsert(accountRedeem);
        
        AccountAssetPO cashAsset = new AccountAssetPO();

        cashAsset.setAssetSource(AssetSourceEnum.NORMALFEE);
        cashAsset.setAccountId(accountRedeem.getAccountId());
        cashAsset.setConfirmTime(DateUtils.now());
        cashAsset.setConfirmShare(BigDecimal.ZERO);
        cashAsset.setApplyMoney(accountRedeem.getApplyMoney());
        //cashAsset.setConfirmMoney(confirmMoney);
        cashAsset.setConfirmMoney(confirmMoney);
        cashAsset.setApplyTime(DateUtils.now());
        cashAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
        cashAsset.setProductCode(Constants.CASH);
        cashAsset.setCreateTime(accountRedeem.getCreateTime());
        accountAssetService.updateOrInsert(cashAsset);

    return confirmMoney;
}
    
    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            calculateAssetFundNav();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }
}
