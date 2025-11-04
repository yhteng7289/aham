package com.pivot.aham.api.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pivot.aham.api.server.dto.ModelRecommendDTO;
import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.server.dto.ProductInfoResDTO;
import com.pivot.aham.api.server.dto.req.SaxoTradeReq;
import com.pivot.aham.api.server.dto.resp.SaxoTradeResult;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.service.job.TradeAnalysisStrategy;
import com.pivot.aham.api.service.job.wrapperbean.BuyEtfTmpOrderBean;
import com.pivot.aham.api.service.job.wrapperbean.EtfBean;
import com.pivot.aham.api.service.job.wrapperbean.EtfListBean;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.api.service.support.AccountAssetStatisticBean;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.AccountTypeEnum;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import com.pivot.aham.common.enums.ModelStatusEnum;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.ProductMainSubTypeEnum;
import com.pivot.aham.common.enums.RedeemTypeEnum;
import com.pivot.aham.common.enums.TmpOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.AssetSourceEnum;
import com.pivot.aham.common.enums.analysis.EtfExecutedStatusEnum;
import com.pivot.aham.common.enums.analysis.InitDayEnum;
import com.pivot.aham.common.enums.analysis.RechargeOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.RedeemOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.TmpOrderExecuteStatusEnum;

import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 交易下单分析
 *
 * @author addison
 * @since 2018年12月17日
 */
@Service
@Slf4j
public class TradeAnalysisStrategyImpl implements TradeAnalysisStrategy {

    /**
     * 日志记录器
     */
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * 用于计算超额现金
     */
    public static final String THREEPRECENT = "0.03";
    /**
     * 用于计算超额现金
     */
    public static final String NIFIPRECENT = "0.95";

    @Autowired
    private AccountAssetService accountAssetService;
    @Autowired
    private AssetFundNavService accountNavService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;
    @Autowired
    private TmpOrderRecordService tmpOrderRecordService;
    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Autowired
    private AccountRedeemService accountRedeemService;
    @Resource
    private AssetFundNavService assetFundNavService;
    @Autowired
    private RedeemApplyService bankVARedeemService;
    @Resource
    private AnalysisSupportService analysisSupportService;
    @Resource
    private TpcfTncfService tpcfTncfService;
    @Resource
    private AccountEtfSharesService accountEtfSharesService;
    @Resource
    private RedeemApplyService redeemApplyService;
    @Autowired
    private AccountRechargeService accountRechargeService;

    @Override
    public void onlyWithdrawaltradeAnalysis(BigDecimal totalUnBuy,
            BigDecimal tncf,
            AccountInfoPO accountInfo,
            List<AccountRedeemPO> accountRedeemPOs) {
        BigDecimal shouldRedeemAmount = tncf.subtract(totalUnBuy);
        //根据navtime获取昨日账户资产
        AccountFundNavPO accountNavQuery = new AccountFundNavPO();
        accountNavQuery.setAccountId(accountInfo.getId());

        //accountNavQuery.setNavTime(DateUtils.now());
        accountNavQuery.setNavTime(DateUtils.addDateByDay(DateUtils.now(), -1));
        AccountFundNavPO accountNav = accountNavService.selectOneByNavTime(accountNavQuery);
        if (accountNav == null) {
            LOGGER.error("账户{},找不到对应账户基金净值记录", accountInfo.getId());
            throw new BusinessException("账户" + accountInfo.getId() + ",找不到对应账户基金净值记录");
        }

        BigDecimal yesterDayTotalAsset = accountNav.getTotalAsset();
        if (yesterDayTotalAsset.compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.error("账户{},昨日总资产异常:小于等于0,昨日总资产{}", accountInfo.getId(), yesterDayTotalAsset);
            throw new BusinessException("账户" + accountInfo.getId() + ",昨日总资产小于等于0");
        }
        LOGGER.info("只有提现资金流,账号{},昨日总资产{}", JSON.toJSONString(accountInfo), yesterDayTotalAsset);
        //获取账户资产详情
        AccountAssetPO assetQuery = new AccountAssetPO();
        assetQuery.setAccountId(accountInfo.getId());
        List<AccountAssetPO> accountAssetList = accountAssetService.queryList(assetQuery);
        LOGGER.info("账户{},统计前资产明细{}", accountInfo.getId(), JSON.toJSON(accountAssetList));
        if (CollectionUtils.isEmpty(accountAssetList)) {
            log.info("账户" + accountInfo.getId() + "该账已无资产");
            return;
        }

        //昨日收盘价
        Date closePriceYesterday = DateUtils.addDateByDay(DateUtils.now(), -2);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(closePriceYesterday);
        LOGGER.info("date:{},etf收市价:{}=======", closePriceYesterday, JSON.toJSON(etfClosingPriceMap));
        if (etfClosingPriceMap == null) {
            throw new BusinessException("昨日收市价为空");
        }
        List<AccountAssetStatisticBean> accountAssetStatisticBeans = AccountAssetStatistic.statAccountAsset(accountAssetList, etfClosingPriceMap);
        LOGGER.info("账户{},统计后资产明细{}", accountInfo.getId(), JSON.toJSON(accountAssetStatisticBeans));
        if (CollectionUtils.isEmpty(accountAssetStatisticBeans)) {
            log.info("账户" + accountInfo.getId() + "该投资账户无持有中资产");
            return;
        }

        BigDecimal availableCashOnHand = BigDecimal.ZERO;

        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeans) {
            if (accountAssetStatisticBean.getProductCode().equals(Constants.CASH)) {
                availableCashOnHand = accountAssetStatisticBean.getProductMoney();
                break;
            }
        }
        // Total Cash on hand reserve 95% cash on hand
        BigDecimal conditionCashToMeet = availableCashOnHand.multiply(new BigDecimal(NIFIPRECENT)).setScale(3, RoundingMode.HALF_DOWN);
        BigDecimal cashToWithdraw = tncf.setScale(3, RoundingMode.HALF_DOWN);
        Double differential = conditionCashToMeet.subtract(cashToWithdraw).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
        log.info("conditionCashToMeet {} ", conditionCashToMeet);
        log.info("cashToWithdraw {} ", cashToWithdraw);
        log.info("differential {} ", differential);
        if (differential > 0.00) {
            // Directly withdraw from cash - Dont care about the etf calculation
            log.info("accountRedeemPOs {} , totalUnBuy {} , accountInfo.getId() {} ,  tncf ", accountRedeemPOs, totalUnBuy, accountInfo.getId(), tncf);
            handleExceedCashRedeem(accountRedeemPOs, totalUnBuy, accountInfo.getId(), tncf, true);
            return;
        }

        Map<String, AccountAssetStatisticBean> allHoldMultimap = Maps.newHashMap();
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeans) {
            allHoldMultimap.put(accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean);
        }

        //请求执行器下提现申请单
        //赎回金额<=(CashExcess_SAXO = ADJ_Cash_SAXO_USD(t-1)-ADJ_Fund_Asset(t-1)*3%)*0.95,直接从saxo现金账户提现
        //计算超额现金=(昨日cash-(昨日总资产*0.03))*0.95
        BigDecimal yesterDayTotalCash = accountNav.getTotalCash();
        log.info("yesterDayTotalCash {} ", yesterDayTotalCash);
        BigDecimal totalAssetPre = yesterDayTotalAsset.multiply(new BigDecimal(THREEPRECENT));
        log.info("totalAssetPre {} ", totalAssetPre);
        BigDecimal cashExcess = yesterDayTotalCash.subtract(totalAssetPre);
        log.info("cashExcess {} ", cashExcess);
        BigDecimal cashExcessPrecent = cashExcess.multiply(new BigDecimal(NIFIPRECENT));
        log.info("cashExcessPrecent {} ", cashExcessPrecent);
//        //记录过程数据
//        StaticFortradeAnalysisEvent staticFortradeAnalysisEvent = new StaticFortradeAnalysisEvent();
//        staticFortradeAnalysisEvent.setAccountId(accountInfo.getId());
//        staticFortradeAnalysisEvent.setExcessCash(cashExcess);
//        eventBus.post(staticFortradeAnalysisEvent);

        LOGGER.info("账户{},总资产{},该提现金额{},超额现金{},超额现金的95%:{}", accountInfo.getId(), yesterDayTotalAsset, shouldRedeemAmount, cashExcess, cashExcessPrecent);
        // Handle the directly withdrawal as above
//        if (shouldRedeemAmount.compareTo(cashExcessPrecent) <= 0) {
//            LOGGER.info("账户{},提现进入超额限额范围内提现,不用下单,diff:{}", accountInfo.getId(), shouldRedeemAmount);
////            AccountAssetStatisticBean unBuyHoldAsset = allHoldMultimap.get(Constants.UN_BUY_PRODUCT_CODE);
//            handleExceedCashRedeem(accountRedeemPOs, totalUnBuy, accountInfo.getId(), tncf);
//
//        } else {
        List<ProductInfoResDTO> allProductInfo = modelServiceRemoteService.queryAllProductInfo();
        if (CollectionUtils.isEmpty(allProductInfo)) {
            throw new BusinessException("全量产品信息为空");
        }

        List<ProductInfoResDTO> subList = FluentIterable.from(allProductInfo).filter(new Predicate<ProductInfoResDTO>() {
            @Override
            public boolean apply(@Nullable ProductInfoResDTO o) {
                return o.getProductType() == ProductMainSubTypeEnum.SUB;
            }
        }).toList();
        LOGGER.info("subEtf产品列表明细:{}", JSON.toJSON(subList));

        //模型的sub配比
//            BigDecimal totalSubShares = BigDecimal.ZERO;
        BigDecimal totalSubValue = BigDecimal.ZERO;
        for (ProductInfoResDTO subEtfEntry : subList) {
            String productCode = subEtfEntry.getProductCode();
            if (productCode.equals(Constants.MAIN_CASH) || productCode.equals(Constants.SUB_CASH)) {
                continue;
            }
            AccountAssetStatisticBean accountAssetStat = allHoldMultimap.get(productCode);
            if (accountAssetStat == null) {
                continue;
            }
            BigDecimal productValue = accountAssetStat.getProductMoney();
            totalSubValue = totalSubValue.add(productValue);
        }
        LOGGER.info("账户{},总资产{},该提现金额:{},超额现金:{},总sub资产:{}", accountInfo.getId(), yesterDayTotalAsset, shouldRedeemAmount, cashExcess, totalSubValue);
        BigDecimal subExcess = totalSubValue.multiply(new BigDecimal(NIFIPRECENT));
        log.info("SUB-ETF : subExcess {} ", subExcess);
        //sub的hold资产比例
        Map<String, BigDecimal> subHoldPercent = Maps.newHashMap();

        subHoldPercent = calPercent(allHoldMultimap, subList, totalSubValue);
        log.info("SUB-ETF : subHoldPercent {} ", subHoldPercent);

        //所有的hold资产比例
        BigDecimal totalAllVaule = BigDecimal.ZERO;
        for (ProductInfoResDTO productInfoResDTO : allProductInfo) {
            String productCode = productInfoResDTO.getProductCode();
            if (productCode.equals(Constants.MAIN_CASH) || productCode.equals(Constants.SUB_CASH)) {
                continue;
            }
            AccountAssetStatisticBean accountAssetStat = allHoldMultimap.get(productCode);
            if (accountAssetStat == null) {
                continue;
            }
            BigDecimal productValue = accountAssetStat.getProductMoney();
            totalAllVaule = totalAllVaule.add(productValue);
        }
        Map<String, BigDecimal> allHoldPercent = Maps.newHashMap();
        allHoldPercent = calPercent(allHoldMultimap, allProductInfo, totalAllVaule);
        //判断是否超过etf持有金额,如果超过，只提etf持有金额
        BigDecimal etfHoldAmount = accountNav.getTotalAsset().subtract(accountNav.getTotalCash());
        if (shouldRedeemAmount.compareTo(etfHoldAmount) > 0) {
            shouldRedeemAmount = etfHoldAmount;
        }
        LOGGER.info("账户{},总资产{},该提现金额{},超额现金{},sub+main资产{}", accountInfo.getId(), yesterDayTotalAsset, shouldRedeemAmount, cashExcess, totalAllVaule);
        LOGGER.info("账户{},sub比例:{}", accountInfo.getId(), JSON.toJSONString(subHoldPercent));
        LOGGER.info("账户{},main比例:{}", accountInfo.getId(), JSON.toJSONString(allHoldPercent));

        //判断是否为tailor且为全赎
        //根据accountId查询RedeemApply
        //查询这个用户是否有全部赎回订单
        Boolean isTailorAllRedeem = false;
        if (accountInfo.getInvestType() == AccountTypeEnum.TAILOR) {
            RedeemApplyPO redeemApplyQuery = new RedeemApplyPO();
            redeemApplyQuery.setAccountId(accountInfo.getId());
            redeemApplyQuery.setRedeemType(RedeemTypeEnum.ALLRedeem);
            redeemApplyQuery.setEtfExecutedStatus(EtfExecutedStatusEnum.DEFAULT);
            RedeemApplyPO redeemApply = redeemApplyService.selectOne(redeemApplyQuery);
            if (redeemApply != null) {
                isTailorAllRedeem = true;
            }
        }

        //<=EV_SUB*0.95
        log.info("shouldRedeemAmount {} , subExcess {} ", shouldRedeemAmount, subExcess);
        if (shouldRedeemAmount.compareTo(subExcess) <= 0) {
            handlerOrder(accountInfo, shouldRedeemAmount, subHoldPercent, isTailorAllRedeem, allHoldMultimap);
        } else if (shouldRedeemAmount.compareTo(subExcess) > 0) {
            //>EV_SUB*0.95
            handlerOrder(accountInfo, shouldRedeemAmount, allHoldPercent, isTailorAllRedeem, allHoldMultimap);
        }
        //充值大于0 需要处理对冲的资产
        handelBalanceMoney(accountInfo.getId(), totalUnBuy);

//        }
    }

    /**
     * TPCF > 0 & TNCK < 0 , 下单回调的时候所有的Unbuy转cash
     *
     * @param accountId
     * @param totalUnbuy
     */
    private void handelBalanceMoney(Long accountId, BigDecimal totalUnbuy) {
        //TPCF
        List<AccountRechargePO> accountRechargePOs = tpcfTncfService.getAccountTpcf(accountId);
        BigDecimal balanceMoney = tpcfTncfService.getAccountTpcfMoney(accountRechargePOs);
        if (balanceMoney.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        //所有的 Tpcf 转 cash
        List<AccountAssetPO> insertUnBuyList = Lists.newArrayList();
        if (balanceMoney.compareTo(BigDecimal.ZERO) > 0) {
            Long totalTmpOrder = Sequence.next();
            AccountAssetPO outAsset = new AccountAssetPO();
            outAsset.setAssetSource(AssetSourceEnum.SELLCROSS);
            outAsset.setAccountId(accountId);
            outAsset.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
            outAsset.setConfirmShare(BigDecimal.ZERO);
            outAsset.setConfirmMoney(totalUnbuy);
            outAsset.setApplyMoney(totalUnbuy);
            outAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
            outAsset.setRechargeOrderNo(0L);
            outAsset.setApplyTime(DateUtils.now());
            outAsset.setConfirmTime(DateUtils.now());
            outAsset.setTotalTmpOrderId(totalTmpOrder);
            outAsset.setUpdateTime(DateUtils.now());
            outAsset.setCreateTime(DateUtils.now());
            outAsset.setTmpOrderId(0L);
            insertUnBuyList.add(outAsset);
            //对冲充值资产处理

            AccountAssetPO inAsset = new AccountAssetPO();
            inAsset.setAssetSource(AssetSourceEnum.BUYCROSS);
            inAsset.setAccountId(accountId);
            inAsset.setConfirmShare(BigDecimal.ZERO);
            inAsset.setConfirmMoney(totalUnbuy);
            inAsset.setApplyMoney(totalUnbuy);
            inAsset.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
            inAsset.setRechargeOrderNo(0L);
            inAsset.setApplyTime(DateUtils.now());
            inAsset.setConfirmTime(DateUtils.now());
            inAsset.setTotalTmpOrderId(totalTmpOrder);
            inAsset.setCreateTime(DateUtils.now());
            inAsset.setUpdateTime(DateUtils.now());
            inAsset.setTmpOrderId(Sequence.next());
            inAsset.setProductCode(Constants.CASH);
            insertUnBuyList.add(inAsset);
//            analysisSupportService.handelRechargeAndAsset(updateUnBuyList, insertUnBuyList);
            accountAssetService.insertBatch(insertUnBuyList);
        }

    }

    /**
     * @param accountRedeemPOs
     * @param totalUnbuy
     * @param accountId
     * @param tncf
     */
    private void handleExceedCashRedeem(List<AccountRedeemPO> accountRedeemPOs, BigDecimal totalUnbuy, Long accountId, BigDecimal tncf) {
        handleExceedCashRedeem(accountRedeemPOs, totalUnbuy, accountId, tncf, false);
    }

    /**
     * @param accountRedeemPOs
     * @param totalUnbuy
     * @param accountId
     * @param tncf
     */
    private void handleExceedCashRedeem(List<AccountRedeemPO> accountRedeemPOs, BigDecimal totalUnbuy, Long accountId, BigDecimal tncf, Boolean isDirectRedeem) {
        //查询充值流水
        List<AccountRechargePO> accountRechargePOs = tpcfTncfService.getAccountTpcf(accountId);
        log.info("handleExceedCashRedeem , accountRechargePOs {} ", accountRechargePOs);
        BigDecimal accountTpcfMoney = tpcfTncfService.getAccountTpcfMoney(accountRechargePOs);
        log.info("handleExceedCashRedeem , accountTpcfMoney {} ", accountTpcfMoney);
        BigDecimal yesterdayUnBuy = totalUnbuy.subtract(accountTpcfMoney).setScale(6, BigDecimal.ROUND_HALF_UP);
        log.info("handleExceedCashRedeem , yesterdayUnBuy {} ", yesterdayUnBuy);
        // Money redeem bigger than Yesterday Unbuy
        if (tncf.compareTo(yesterdayUnBuy) >= 0) {
            /**
             * tncf 金额大于昨日累加额unBuy, 所有的uNBuy 都要变成 cash 供用户提现的时候从 cash 出
             */
            log.info("tncf.compareTo(yesterdayUnBuy)");
            if (isDirectRedeem) {
                // Find out all the cash avaibale in an account to make sure direct redeem;
                BigDecimal totalCashAvailable = accountTpcfMoney.add(yesterdayUnBuy).setScale(6, BigDecimal.ROUND_HALF_UP);
                if (totalCashAvailable.compareTo(tncf) > 0) {
                    handelUnBuyEtfAdjCash(accountRedeemPOs, yesterdayUnBuy, accountId);
                } else {
                    // Nothing to convert . Put it as 0. Let account redeem mark it success;
                    handelUnBuyEtfAdjCash(accountRedeemPOs, new BigDecimal(0), accountId);
                }
            } else {
                handelUnBuyEtfAdjCash(accountRedeemPOs, yesterdayUnBuy, accountId);
            }
        } else {
            /**
             * tncf 金额小于昨日累加的unBuy, 需要剩余部分unBuy 剩余的unBuy = unBuy(t-1) - diff
             * 需要转换成cash的unBuy = totalUnBuy - 剩余的unBuy
             *
             */
            BigDecimal remainUnbuy = yesterdayUnBuy.subtract(tncf).setScale(6, BigDecimal.ROUND_HALF_UP);
            log.info("handleExceedCashRedeem , remainUnbuy {} ", remainUnbuy);
            BigDecimal transferUnBuy = totalUnbuy.subtract(remainUnbuy);
            log.info("handleExceedCashRedeem , transferUnBuy {} ", transferUnBuy);
            handelUnBuyEtfAdjCash(accountRedeemPOs, transferUnBuy, accountId);
        }
    }

    /**
     * 处理提现记录
     *
     * @param accountInfo
     * @param shouldRedeemAmount
     * @param productPercent
     */
    private void handlerOrder(AccountInfoPO accountInfo,
            BigDecimal shouldRedeemAmount,
            Map<String, BigDecimal> productPercent,
            Boolean isTailorAllRedeem,
            Map<String, AccountAssetStatisticBean> allHoldMultimap) {
        Set<Map.Entry<String, BigDecimal>> entrySet = productPercent.entrySet();
        Long totalId = Sequence.next();
        LOGGER.info("账户{},提现金额{},总订单号{}", accountInfo.getId(), shouldRedeemAmount, totalId);
        //todo 以下三个表操作集成到统一事务
        //tailor全部赎回
        if (isTailorAllRedeem) {
            genTailorAllRedeemTmpOrder(accountInfo, allHoldMultimap, totalId);
        } else {//tailor非全部赎回
            genTmpOrder(accountInfo, shouldRedeemAmount, entrySet, totalId);
        }
        //更新accountRedeem
        updateAccountRedeem(accountInfo, totalId);
//        更新提现申请
//        updateVApplyRedeem(accountInfo, totalId, EtfExecutedStatusEnum.HANDLING);
    }

    /**
     * 更新accountRedeem
     *
     * @param accountInfo
     * @param totalId
     */
    private void updateAccountRedeem(AccountInfoPO accountInfo, Long totalId) {
        //获取TNCF
        AccountRedeemPO queryParam = new AccountRedeemPO();
        queryParam.setAccountId(accountInfo.getId());
        queryParam.setOrderStatus(RedeemOrderStatusEnum.PROCESSING);
        queryParam.setTncfStatus(TncfStatusEnum.TNCF);
        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.getRedeemListByTime(queryParam);

        for (AccountRedeemPO accountRedeem : accountRedeemPOs) {
            accountRedeem.setAccountId(accountInfo.getId());
            accountRedeem.setTotalTmpOrderId(totalId);
            accountRedeem.setOrderStatus(RedeemOrderStatusEnum.HANDLING);
            accountRedeemService.updateOrInsert(accountRedeem);
            if(accountRedeem.getIsAnnualPerformanceFee().equalsIgnoreCase("N")){ //Added By WooiTatt 20210104
                RedeemApplyPO redeemApply = bankVARedeemService.queryById(accountRedeem.getRedeemApplyId());
                redeemApply.setAccountId(accountInfo.getId());
                redeemApply.setTotalTmpOrderId(totalId);
                redeemApply.setEtfExecutedStatus(EtfExecutedStatusEnum.HANDLING);
                bankVARedeemService.updateOrInsert(redeemApply);
            }
        }
    }

//    /**
//     * 更新accountRedeem
//     *
//     * @param accountInfo
//     * @param totalId
//     */
//    private void updateVApplyRedeem(AccountInfoPO accountInfo, Long totalId, EtfExecutedStatusEnum etfExecutedStatusEnum) {
//        Date endTime = DateUtils.getDate(new Date(), 10, 0, 0);
//        Date startTime = DateUtils.addDays(endTime, -1);
//
//        RedeemApplyPO vaRedeemApplyPO = new RedeemApplyPO();
//        vaRedeemApplyPO.setAccountId(accountInfo.getId());
//        vaRedeemApplyPO.setStartApplyTime(startTime);
//        vaRedeemApplyPO.setEndApplyTime(endTime);
//        vaRedeemApplyPO.setRedeemApplyStatus(RedeemApplyStatusEnum.HANDLING);
//        vaRedeemApplyPO.setWithdrawalSourceType(WithdrawalSourceTypeEnum.FROMGOAL);
//        List<RedeemApplyPO> redeemApplyPOList = bankVARedeemService.queryByApplyTime(vaRedeemApplyPO);
//        log.info("交易分析，tncf:{}",JSON.toJSONString(redeemApplyPOList));
//
//
//        for (RedeemApplyPO redeemApplyPO : redeemApplyPOList) {
//            redeemApplyPO.setAccountId(accountInfo.getId());
//            redeemApplyPO.setTotalTmpOrderId(totalId);
//            redeemApplyPO.setEtfExecutedStatus(etfExecutedStatusEnum);
//            bankVARedeemService.updateByAccountId(redeemApplyPO);
//        }
//    }
    /**
     * 计算比例(etf占总份额比例)
     *
     * @param allHoldMultimap
     * @param weightPrecentEntrySet
     * @param totalVaule
     */
    private Map<String, BigDecimal> calPercent(Map<String, AccountAssetStatisticBean> allHoldMultimap,
            List<ProductInfoResDTO> weightPrecentEntrySet,
            BigDecimal totalVaule) {
        Map<String, BigDecimal> subHoldPercent = Maps.newHashMap();
        for (ProductInfoResDTO subEntry : weightPrecentEntrySet) {
            String productCode = subEntry.getProductCode();
            AccountAssetStatisticBean accountAssetStat = allHoldMultimap.get(productCode);
            if (accountAssetStat == null) {
                continue;
            }
            BigDecimal precent = accountAssetStat.getProductMoney().divide(totalVaule, 6, BigDecimal.ROUND_HALF_UP);
            subHoldPercent.put(productCode, precent);
        }
        return subHoldPercent;
    }

    /**
     * tailor账户全部提现
     *
     * @param accountInfo
     * @param allHoldMultimap
     * @param totalId
     */
    private void genTailorAllRedeemTmpOrder(AccountInfoPO accountInfo,
            Map<String, AccountAssetStatisticBean> allHoldMultimap,
            Long totalId) {

        log.info("tail账户id:{}现金资产不进行操作:{}", accountInfo.getId(), JSON.toJSONString(allHoldMultimap));
        for (Map.Entry<String, AccountAssetStatisticBean> accountAssetStatisticBeanEntry : allHoldMultimap.entrySet()) {
            String productCode = accountAssetStatisticBeanEntry.getKey();
            if (productCode.equals(Constants.MAIN_CASH) || productCode.equals(Constants.SUB_CASH) || productCode.equals(Constants.CASH)) {
                continue;
            }
            AccountAssetStatisticBean accountAssetStatisticBean = accountAssetStatisticBeanEntry.getValue();
            TmpOrderRecordPO tmpOrderRecordPO = new TmpOrderRecordPO();
            tmpOrderRecordPO.setAccountId(accountInfo.getId());

            tmpOrderRecordPO.setActionType(TmpOrderActionTypeEnum.SELL);
//            BigDecimal ertfPrecent = etfPrecentEntry.getValue();
            BigDecimal etfRedeemMoney = accountAssetStatisticBean.getProductMoney();
            tmpOrderRecordPO.setApplyMoney(etfRedeemMoney);

            tmpOrderRecordPO.setApplyTime(DateUtils.now());
            tmpOrderRecordPO.setTotalTmpOrderId(totalId);
            tmpOrderRecordPO.setProductCode(productCode);
            Long tmpOrder = Sequence.next();
            tmpOrderRecordPO.setTmpOrderId(tmpOrder);
            tmpOrderRecordPO.setTmpOrderTradeType(EtfOrderTypeEnum.GSA);
            tmpOrderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.CREATE);

            tmpOrderRecordPO = tmpOrderRecordService.updateOrInsert(tmpOrderRecordPO);

            //请求执行器下单
            SaxoTradeReq tradeItemDTO = new SaxoTradeReq();
            tradeItemDTO.setAccountId(accountInfo.getId());
            tradeItemDTO.setAmount(etfRedeemMoney);
            tradeItemDTO.setEtfCode(productCode);
            tradeItemDTO.setOutBusinessId(tmpOrder);
            LOGGER.info("tailor全部赎回远程调用saxoTradeRemoteService.sell,入参:{}", JSON.toJSONString(tradeItemDTO));

            //查询账户某个etf的资产
            tradeItemDTO.setOrderType(EtfOrderTypeEnum.GSA);
//            }

            RpcMessage<SaxoTradeResult> rpcMessage = saxoTradeRemoteService.sell(tradeItemDTO);
            LOGGER.info("tailor全部赎回远程调用saxoTradeRemoteService.sell,出参:{}", JSON.toJSONString(rpcMessage));
            if (rpcMessage.getResultCode() == RpcMessageStandardCode.OK.value()) {
                //更新执行单号
                tmpOrderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.HANDLING);
                tmpOrderRecordPO.setExecuteOrderId(rpcMessage.getContent().getOrderId());
                tmpOrderRecordService.updateOrInsert(tmpOrderRecordPO);
            } else {
                throw new BusinessException("tailor执行器下单失败");
            }

        }
    }

    /**
     * 产生零时订单（每个etf一单）
     *
     * @param accountInfo
     * @param shouldRedeemAmount
     * @param etfPrecentEntrySet
     * @param totalId
     */
    private void genTmpOrder(AccountInfoPO accountInfo, BigDecimal shouldRedeemAmount, Set<Map.Entry<String, BigDecimal>> etfPrecentEntrySet, Long totalId) {
        log.info("tail账户id:{}现金资产不进行操作:{}", accountInfo.getId(), JSON.toJSONString(etfPrecentEntrySet));
        for (Map.Entry<String, BigDecimal> etfPrecentEntry : etfPrecentEntrySet) {
            String productCode = etfPrecentEntry.getKey();
            if (productCode.equals(Constants.MAIN_CASH) || productCode.equals(Constants.SUB_CASH) || productCode.equals(Constants.CASH)) {
                continue;
            }
            TmpOrderRecordPO tmpOrderRecordPO = new TmpOrderRecordPO();
            tmpOrderRecordPO.setAccountId(accountInfo.getId());

            tmpOrderRecordPO.setActionType(TmpOrderActionTypeEnum.SELL);

            BigDecimal ertfPrecent = etfPrecentEntry.getValue();
            BigDecimal etfRedeemMoney = shouldRedeemAmount.multiply(ertfPrecent);

            //查询账户某个etf的资产
            //Date lastDate = DateUtils.addDateByDay(DateUtils.now(), 0);
            Date lastDate = DateUtils.addDateByDay(DateUtils.now(), -1);
            AccountEtfSharesPO accountEtfSharesPO = new AccountEtfSharesPO();
            accountEtfSharesPO.setProductCode(productCode);
            accountEtfSharesPO.setAccountId(accountInfo.getId());
            accountEtfSharesPO.setStaticDate(lastDate);
            log.info("获取account的etf持有:{}", JSON.toJSONString(accountEtfSharesPO));
            AccountEtfSharesPO accountEtfShares = accountEtfSharesService.selectByStaticDateByAccountId(accountEtfSharesPO);
            BigDecimal holdMoney = accountEtfShares.getMoney();
            EtfOrderTypeEnum etfOrderTypeEnum = EtfOrderTypeEnum.GSP;
//            tradeItemDTO.setOrderType(EtfOrderTypeEnum.GSP);
            if (etfRedeemMoney.compareTo(holdMoney) >= 0) {
                etfOrderTypeEnum = EtfOrderTypeEnum.GSA;
            }

            tmpOrderRecordPO.setApplyMoney(etfRedeemMoney);

            tmpOrderRecordPO.setApplyTime(DateUtils.now());
            tmpOrderRecordPO.setTotalTmpOrderId(totalId);
            tmpOrderRecordPO.setProductCode(etfPrecentEntry.getKey());
            Long tmpOrder = Sequence.next();
            tmpOrderRecordPO.setTmpOrderId(tmpOrder);
            tmpOrderRecordPO.setTmpOrderTradeType(etfOrderTypeEnum);
            tmpOrderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.CREATE);

            tmpOrderRecordPO = tmpOrderRecordService.updateOrInsert(tmpOrderRecordPO);

            //请求执行器下单
            SaxoTradeReq tradeItemDTO = new SaxoTradeReq();
            tradeItemDTO.setAccountId(accountInfo.getId());
            tradeItemDTO.setAmount(etfRedeemMoney);
            tradeItemDTO.setEtfCode(etfPrecentEntry.getKey());
            tradeItemDTO.setOutBusinessId(tmpOrder);
            LOGGER.info("远程调用saxoTradeRemoteService.sell,入参:{}", JSON.toJSONString(tradeItemDTO));
            tradeItemDTO.setOrderType(etfOrderTypeEnum);
            RpcMessage<SaxoTradeResult> rpcMessage = saxoTradeRemoteService.sell(tradeItemDTO);
            LOGGER.info("远程调用saxoTradeRemoteService.sell,出参:{}", JSON.toJSONString(rpcMessage));
            if (rpcMessage.getResultCode() == RpcMessageStandardCode.OK.value()) {
                //更新执行单号
                tmpOrderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.HANDLING);
                tmpOrderRecordPO.setExecuteOrderId(rpcMessage.getContent().getOrderId());
                tmpOrderRecordService.updateOrInsert(tmpOrderRecordPO);
            } else {
                throw new BusinessException("执行器下单失败");
            }

        }
    }

    /**
     * 1、充值大于提现 或者 只有充值 2、只有充值: 2.1、如果小于 3000 就是 unBuy 2.2、如果大于 3000 就是 下ETF买单
     * 3、既有充值,又有提现。 3.1、diff 3.2、 3.3、
     *
     * @param totalUnbuy
     * @param totalRedeem
     * @param accountInfo
     * @param accountRedeemPOs
     */
    @Override
    public void onlyRechargeTradeAnalysis(BigDecimal totalUnbuy,
            BigDecimal totalRedeem,
            AccountInfoPO accountInfo,
            List<AccountRedeemPO> accountRedeemPOs) {
        BigDecimal buyEtfMoney = totalUnbuy.subtract(totalRedeem);
        //大于申购Etf的限制才进行发单
        if (buyEtfMoney.compareTo(new BigDecimal(Constants.BUY_ETF_LIMIT)) < 0) {
            LOGGER.info("账户accountId:{},交易分析充值减提现余额不足,不发起etf交易,totalUnbuy:{},totalRedeem:{}",
                    accountInfo.getId(), totalUnbuy, totalRedeem);
            //buy > sell 不下ETF单,调整 unBuy + 提现状态(t_account_redeem、t_redeem_apply)
            if (totalRedeem.compareTo(BigDecimal.ZERO) > 0) {
                handelUnBuyEtfAdjCash(accountRedeemPOs, totalRedeem, accountInfo.getId());
                Long totalTmpOrderId = Sequence.next();
                updateAccountRecharge(accountInfo.getId(), totalTmpOrderId);
            }
            return;
        }
        ModelRecommendDTO modelRecommendDTO = new ModelRecommendDTO();
        modelRecommendDTO.setPortfolioId(accountInfo.getPortfolioId());
        modelRecommendDTO.setModelStatus(ModelStatusEnum.Effective);
        ModelRecommendResDTO modelRecommendRes = modelServiceRemoteService.queryValidModelByPortfolioId(modelRecommendDTO);
        if (null == modelRecommendRes) {
            LOGGER.info("账户accountId:{},交易分析,模型数据不存在,totalUnbuy:{},totalRedeem:{}", accountInfo.getId(), totalUnbuy, totalRedeem);
            ErrorLogAndMailUtil.logError(log, "交易分析,模型数据不存在");
            return;
        }
        String productWeight = modelRecommendRes.getProductWeight();
        EtfListBean etfListBean = JSON.parseObject(productWeight, new TypeReference<EtfListBean>() {
        });
        List<EtfBean> weight = Lists.newArrayList();
        //if (InitDayEnum.INIT_DAY == accountInfo.getInitDay()) {
        weight = etfListBean.getMainEtf();
        //} else {
        //    weight = etfListBean.getSubEtf();
        //}
        transferAccountMoney(weight, accountInfo, buyEtfMoney, totalRedeem);
    }

    /**
     * diff 大于 0 需要处理 unBuy 转换成 cash (unBuy的出 + cash的入,这里的转换为了用户在计算完NAV之后计算提现金额)
     * redeem 的状态修改 (t_redeem_account、t_redeem_apply)
     *
     * @param accountRedeemPOs
     * @param mergeMoney
     * @param accountId
     */
    @Override
    public void handelUnBuyEtfAdjCash(List<AccountRedeemPO> accountRedeemPOs, BigDecimal mergeMoney, Long accountId) {
        List<AccountAssetPO> accountAssetPOList = Lists.newArrayList();
        log.info("handelUnBuyEtfAdjCash , mergeMoney {} ", mergeMoney);
        if (mergeMoney.compareTo(BigDecimal.ZERO) > 0) {
            Long totalTmpOrderId = Sequence.next();
            AccountAssetPO outAsset = new AccountAssetPO();
            outAsset.setAssetSource(AssetSourceEnum.SELLCROSS);
            outAsset.setAccountId(accountId);
            outAsset.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
            outAsset.setConfirmShare(BigDecimal.ZERO);
            outAsset.setConfirmMoney(mergeMoney);
            outAsset.setApplyMoney(mergeMoney);
            outAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
            outAsset.setRechargeOrderNo(0L);
            outAsset.setApplyTime(DateUtils.now());
            outAsset.setConfirmTime(DateUtils.now());
            outAsset.setTotalTmpOrderId(totalTmpOrderId);
            outAsset.setUpdateTime(DateUtils.now());
            outAsset.setCreateTime(DateUtils.now());
            outAsset.setTmpOrderId(0L);
            accountAssetPOList.add(outAsset);

            AccountAssetPO inAsset = new AccountAssetPO();
            inAsset.setAssetSource(AssetSourceEnum.BUYCROSS);
            inAsset.setAccountId(accountId);
            inAsset.setProductCode(Constants.CASH);
            inAsset.setConfirmShare(BigDecimal.ZERO);
            inAsset.setConfirmMoney(mergeMoney);
            inAsset.setApplyMoney(mergeMoney);
            inAsset.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
            inAsset.setRechargeOrderNo(0L);
            inAsset.setApplyTime(DateUtils.now());
            inAsset.setConfirmTime(DateUtils.now());
            inAsset.setTotalTmpOrderId(totalTmpOrderId);
            inAsset.setUpdateTime(DateUtils.now());
            inAsset.setCreateTime(DateUtils.now());
            inAsset.setTmpOrderId(0L);
            accountAssetPOList.add(inAsset);
        }

        analysisSupportService.handelUnBuyEtfAdjCash(accountAssetPOList, accountRedeemPOs);

    }

    /**
     * diff 大于 0 需要处理 unBuy 转换成 cash (unBuy的出 + cash的入,这里的转换为了用户在计算完NAV之后计算提现金额)
     * redeem 的状态修改 (t_redeem_account、t_redeem_apply)
     *
     * @param accountRedeemPOs
     * @param mergeMoney
     * @param accountId
     */
    @Override
    public void handelDirectWithdrawCash(List<AccountRedeemPO> accountRedeemPOs,
            BigDecimal mergeMoney, Long accountId) {
        List<AccountAssetPO> accountAssetPOList = Lists.newArrayList();
        log.info("handelDirectWithdrawCash , mergeMoney {} ", mergeMoney);
        if (mergeMoney.compareTo(BigDecimal.ZERO) > 0) {
            Long totalTmpOrderId = Sequence.next();
            AccountAssetPO outAsset = new AccountAssetPO();
            outAsset.setAssetSource(AssetSourceEnum.CASHWITHDRAWAL);
            outAsset.setAccountId(accountId);
            outAsset.setProductCode(Constants.CASH);
            outAsset.setConfirmShare(BigDecimal.ZERO);
            outAsset.setConfirmMoney(mergeMoney);
            outAsset.setApplyMoney(mergeMoney);
            outAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
            outAsset.setRechargeOrderNo(0L);
            outAsset.setApplyTime(DateUtils.now());
            outAsset.setConfirmTime(DateUtils.now());
            outAsset.setTotalTmpOrderId(totalTmpOrderId);
            outAsset.setUpdateTime(DateUtils.now());
            outAsset.setCreateTime(DateUtils.now());
            outAsset.setTmpOrderId(0L);
            accountAssetPOList.add(outAsset);
        }

        analysisSupportService.handelUnBuyEtfAdjCash(accountAssetPOList, accountRedeemPOs);

    }

    /**
     * 充值大于提现 --> 发起申购单 1.首先判断是采用哪种模型 main、sub 2.构造申请流水 3.申请中资产 4.发送申请
     *
     * @param weight
     * @param accountInfo
     * @param buyEtfMoney
     */
    private void transferAccountMoney(List<EtfBean> weight,
            AccountInfoPO accountInfo,
            BigDecimal buyEtfMoney, BigDecimal mergeMoney) {
        LOGGER.info("accountID:{},发送Etf单,weight:{},buyEtfMoney:{}", accountInfo.getId(), JSON.toJSON(weight), buyEtfMoney);
        List<TmpOrderRecordPO> tmpOrderRecordList = Lists.newArrayList();
        //转账回调没有设置totalTmpOrderId 因为不确定accountId。所以发单的时候才记录
        Long totalTmpOrderId = Sequence.next();
        for (EtfBean etfBean : weight) {
            //每支etf向下取整,残渣全部加在cash上
            BigDecimal amount = etfBean.getWeight().multiply(buyEtfMoney).setScale(6, BigDecimal.ROUND_DOWN);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                LOGGER.error("账户accountId:{},发送Etf单,根据配置计算的金额为负值etfBean:{},applyMoney:{}",
                        accountInfo.getId(), JSON.toJSON(etfBean), amount);
                continue;
            }
            TmpOrderRecordPO tmpOrderRecord = constractTmpOrderRecord(etfBean.getEtf(), amount, accountInfo, totalTmpOrderId);
            tmpOrderRecordList.add(tmpOrderRecord);
        }
        //购买etf、更新申购 etf 返回的执行单号
        LOGGER.info("tmpOrderRecordList:{}", JSON.toJSON(tmpOrderRecordList));
        BuyEtfTmpOrderBean buyEtfTmpOrderBean = buyEtf(tmpOrderRecordList, accountInfo);

        //更新资产流水总订单号, 添加Unbuy支出资产流水, 等回调确认之后修改UnBuy支出状态,添加etf收入资产流水
        handleAssets(buyEtfMoney, totalTmpOrderId, accountInfo.getId(), mergeMoney, buyEtfTmpOrderBean);
        LOGGER.info("accountInfo:"+accountInfo.getId()+" totalTmpOrderId:"+ totalTmpOrderId);
        updateAccountRecharge(accountInfo.getId(), totalTmpOrderId);

    }

    /**
     * 1、更新资产流水总订单号, 2、添加Unbuy支出资产流水,处理中 3、等回调确认之后修改UnBuy支出状态,添加etf收入资产流水
     *
     * @param buyEtfMoney
     * @param totalTmpOrderId
     * @param accountId
     * @param mergeMoney
     * @param buyEtfTmpOrderBean
     */
    private void handleAssets(BigDecimal buyEtfMoney,
            Long totalTmpOrderId,
            Long accountId,
            BigDecimal mergeMoney,
            BuyEtfTmpOrderBean buyEtfTmpOrderBean) {
        BigDecimal failApplyMoney = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(buyEtfTmpOrderBean.getFailTmpOrders())) {
            for (TmpOrderRecordPO failTmpOrder : buyEtfTmpOrderBean.getFailTmpOrders()) {
                failApplyMoney = failApplyMoney.add(failTmpOrder.getApplyMoney()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        BigDecimal reallyBuyEtfMoney = buyEtfMoney.subtract(failApplyMoney).setScale(6, BigDecimal.ROUND_HALF_UP);
        log.info("accountId:{},buyEtfMoney:{},failApplyMoney:{},reallyBuyEtfMoney:{}", accountId, buyEtfMoney, failApplyMoney, reallyBuyEtfMoney);

        List<AccountAssetPO> accountAssetPOs = Lists.newArrayList();
        AccountAssetPO outAsset = new AccountAssetPO();
        outAsset.setAssetSource(AssetSourceEnum.BUYETF);
        outAsset.setAccountId(accountId);
        outAsset.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
        outAsset.setConfirmShare(BigDecimal.ZERO);
        outAsset.setConfirmMoney(reallyBuyEtfMoney);
        outAsset.setApplyMoney(reallyBuyEtfMoney);
        outAsset.setProductAssetStatus(ProductAssetStatusEnum.SELL_ING);
        outAsset.setRechargeOrderNo(0L);
        outAsset.setApplyTime(DateUtils.now());
        outAsset.setConfirmTime(DateUtils.now());
        outAsset.setTotalTmpOrderId(totalTmpOrderId);
        outAsset.setUpdateTime(DateUtils.now());
        outAsset.setCreateTime(DateUtils.now());
        outAsset.setTmpOrderId(0L);
        accountAssetPOs.add(outAsset);

        if (mergeMoney.compareTo(BigDecimal.ZERO) > 0) {
            AccountAssetPO outAssetMerge = new AccountAssetPO();
            outAssetMerge.setAssetSource(AssetSourceEnum.SELLCROSS);
            outAssetMerge.setAccountId(accountId);
            outAssetMerge.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
            outAssetMerge.setConfirmShare(BigDecimal.ZERO);
            outAssetMerge.setConfirmMoney(mergeMoney);
            outAssetMerge.setApplyMoney(mergeMoney);
            outAssetMerge.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
            outAssetMerge.setRechargeOrderNo(0L);
            outAssetMerge.setApplyTime(DateUtils.now());
            outAssetMerge.setConfirmTime(DateUtils.now());
            outAssetMerge.setTotalTmpOrderId(totalTmpOrderId);
            outAssetMerge.setUpdateTime(DateUtils.now());
            outAssetMerge.setCreateTime(DateUtils.now());
            outAssetMerge.setTmpOrderId(0L);
            accountAssetPOs.add(outAssetMerge);

            AccountAssetPO inAssetMerge = new AccountAssetPO();
            inAssetMerge.setAssetSource(AssetSourceEnum.BUYCROSS);
            inAssetMerge.setAccountId(accountId);
            inAssetMerge.setProductCode(Constants.CASH);
            inAssetMerge.setConfirmShare(BigDecimal.ZERO);
            inAssetMerge.setConfirmMoney(mergeMoney);
            inAssetMerge.setApplyMoney(mergeMoney);
            inAssetMerge.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
            inAssetMerge.setRechargeOrderNo(0L);
            inAssetMerge.setApplyTime(DateUtils.now());
            inAssetMerge.setConfirmTime(DateUtils.now());
            inAssetMerge.setTotalTmpOrderId(totalTmpOrderId);
            inAssetMerge.setUpdateTime(DateUtils.now());
            inAssetMerge.setCreateTime(DateUtils.now());
            inAssetMerge.setTmpOrderId(0L);
            accountAssetPOs.add(inAssetMerge);
        }
        analysisSupportService.handelBuyEtf(buyEtfTmpOrderBean, accountAssetPOs);
    }

    //购买etf
    private BuyEtfTmpOrderBean buyEtf(List<TmpOrderRecordPO> tmpOrderRecordList, AccountInfoPO accountInfo) {
        List<TmpOrderRecordPO> successTmpOrders = Lists.newArrayList();
        List<TmpOrderRecordPO> failTmpOrders = Lists.newArrayList();

        for (TmpOrderRecordPO tmpOrderRecord : tmpOrderRecordList) {
            if (tmpOrderRecord.getApplyMoney().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            try {
                //先落单在修改状态
                tmpOrderRecordService.saveTmpOrder(tmpOrderRecord);
                if (tmpOrderRecord.getProductCode().equals(Constants.MAIN_CASH)
                        || tmpOrderRecord.getProductCode().equals(Constants.SUB_CASH)
                        || tmpOrderRecord.getProductCode().equals(Constants.CASH)) {
                    //现金先不做处理,后面统一作为残渣收集处理
//                    orderRecordPOs.add(tmpOrderRecord);
                    continue;
                }
                SaxoTradeReq tradeItemDTO = new SaxoTradeReq();
                tradeItemDTO.setAccountId(accountInfo.getId());
                tradeItemDTO.setAmount(tmpOrderRecord.getApplyMoney());
                tradeItemDTO.setEtfCode(tmpOrderRecord.getProductCode());
                tradeItemDTO.setOutBusinessId(tmpOrderRecord.getTmpOrderId());
                tradeItemDTO.setOrderType(EtfOrderTypeEnum.GBA);
                LOGGER.info("accountID:{},发送Etf单,请求参数:{}", accountInfo.getId(), JSON.toJSON(tradeItemDTO));
                RpcMessage<SaxoTradeResult> resultRpcMessage = saxoTradeRemoteService.buy(tradeItemDTO);
                LOGGER.info("accountID:{},发送Etf单,返回结果:{}", accountInfo.getId(), JSON.toJSON(resultRpcMessage));
                if (resultRpcMessage.getResultCode() != RpcMessageStandardCode.OK.value()) {
                    LOGGER.error("accountID:{},发送Etf单,接口调动失败", accountInfo.getId(), JSON.toJSON(resultRpcMessage));
                    tmpOrderRecord.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.SEND_FAIL);
                    failTmpOrders.add(tmpOrderRecord);
                } else {
                    tmpOrderRecord.setExecuteOrderId(resultRpcMessage.getContent().getOrderId());
                    tmpOrderRecord.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.HANDLING);
                    successTmpOrders.add(tmpOrderRecord);
                }
            } catch (Exception ex) {
                LOGGER.error("accountId:{},etfBean:{},发送Etf单异常:", accountInfo.getId(), JSON.toJSON(tmpOrderRecord), ex);
            }
        }

        BuyEtfTmpOrderBean buyEtfTmpOrderBean = new BuyEtfTmpOrderBean();
        buyEtfTmpOrderBean.setFailTmpOrders(failTmpOrders);
        buyEtfTmpOrderBean.setSuccessTmpOrders(successTmpOrders);
        log.info("accountId:{}, etf下申请单，结果：{}", accountInfo.getId(), JSON.toJSONString(buyEtfTmpOrderBean));
        return buyEtfTmpOrderBean;
    }

    /**
     * 构造tmporder
     *
     * @param productCode
     * @param amount
     * @param accountInfo
     * @param totalTmpOrderId
     * @return
     */
    private TmpOrderRecordPO constractTmpOrderRecord(String productCode, BigDecimal amount, AccountInfoPO accountInfo,
            Long totalTmpOrderId) {
        Long orderNo = Sequence.next();
        TmpOrderRecordPO tmpOrderRecordPO = new TmpOrderRecordPO();
        tmpOrderRecordPO.setId(Sequence.next());
        tmpOrderRecordPO.setTmpOrderId(orderNo);
        tmpOrderRecordPO.setTmpOrderTradeType(EtfOrderTypeEnum.GBA);
        tmpOrderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.CREATE);
        tmpOrderRecordPO.setConfirmMoney(BigDecimal.ZERO);
        tmpOrderRecordPO.setProductCode(productCode);
        tmpOrderRecordPO.setActionType(TmpOrderActionTypeEnum.BUY);
        tmpOrderRecordPO.setApplyMoney(amount);
        tmpOrderRecordPO.setApplyTime(DateUtils.now());
        tmpOrderRecordPO.setConfirmTime(DateUtils.now());
//        tmpOrderRecordPO.setConfirmTradePrice(BigDecimal.ZERO);
        tmpOrderRecordPO.setConfirmTradeShares(BigDecimal.ZERO);
        tmpOrderRecordPO.setExecuteOrderId(0L);
        tmpOrderRecordPO.setAccountId(accountInfo.getId());
        tmpOrderRecordPO.setTotalTmpOrderId(totalTmpOrderId);
        tmpOrderRecordPO.setCreateTime(DateUtils.now());
        tmpOrderRecordPO.setUpdateTime(DateUtils.now());
        return tmpOrderRecordPO;
    }
    
     private void updateAccountRecharge(Long accountId, Long totalId) {
        //获取TNCF

        AccountRechargePO queryParam = new AccountRechargePO();
        queryParam.setAccountId(accountId);
        queryParam.setOrderStatus(RechargeOrderStatusEnum.SUCCESS);
        queryParam.setTpcfStatus(TpcfStatusEnum.TPCF);
        queryParam.setTotalTmpOrderId(new Long(0));
        List<AccountRechargePO> lisraccountRechargePO = accountRechargeService.listAccountRecharge(queryParam);
        log.info("lisraccountRechargePO:{}",lisraccountRechargePO);
        for (AccountRechargePO accountRechargePO : lisraccountRechargePO) {
            accountRechargePO.setId(accountRechargePO.getId());
            accountRechargePO.setTotalTmpOrderId(totalId);
            log.info("accountRechargePO:{}",accountRechargePO);
            log.info("accountRechargePOId:{}",accountRechargePO.getId());
            //accountRechargePO.setOrderStatus(RedeemOrderStatusEnum.HANDLING);
            accountRechargeService.updateAccountRecharge(accountRechargePO);
        }
    }

}
