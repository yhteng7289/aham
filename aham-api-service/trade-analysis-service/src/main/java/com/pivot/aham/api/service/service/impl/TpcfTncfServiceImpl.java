package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.pivot.aham.api.service.job.wrapperbean.AnalyTpcfTncfWrapperBean;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.RedeemTypeEnum;
import com.pivot.aham.common.enums.analysis.RechargeOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.RedeemOrderStatusEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Service
@Slf4j
public class TpcfTncfServiceImpl implements TpcfTncfService {

    @Resource
    private AccountRechargeService accountRechargeService;
    @Resource
    private AccountRedeemService accountRedeemService;
    @Resource
    private ExchangeRateService exchangeRateService;
    @Resource
    private RedeemApplyService redeemApplyService;
    @Resource
    private UserFundNavService userFundNavService;
    @Resource
    private AssetFundNavService assetFundNavService;
    @Resource
    private AccountAssetService accountAssetService;
    @Resource
    private AnalysisSupportService analysisSupportService;

    @Override
    public List<AccountRechargePO> getAccountTpcf(Long accountId) {
        AccountRechargePO po = new AccountRechargePO();
        po.setAccountId(accountId);
        po.setOrderStatus(RechargeOrderStatusEnum.SUCCESS);
        po.setTpcfStatus(TpcfStatusEnum.TPCF);
        List<AccountRechargePO> accountRechargePOs = accountRechargeService.listByAccountId(po);
        log.info("accountId:{},tpcfs:{}", accountId, JSON.toJSONString(accountRechargePOs));
        return accountRechargePOs;
    }


    @Override
    public BigDecimal getAccountTncfMoney(Long accountId) {
        List<AccountRedeemPO> accountRedeemPOs = getAccountTncf(accountId);
        BigDecimal applyMoney = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeemPO : accountRedeemPOs) {
            if (accountRedeemPO.getApplyMoney().compareTo(BigDecimal.ZERO) > 0) {
                applyMoney = applyMoney.add(accountRedeemPO.getApplyMoney()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        return applyMoney;
    }

    @Override
    public BigDecimal getAccountHandlingTncfMoney(Long accountId) {
        List<AccountRedeemPO> accountRedeemPOs = getAccountHandlingTncf(accountId);
        BigDecimal applyMoney = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeemPO : accountRedeemPOs) {
            if (accountRedeemPO.getApplyMoney().compareTo(BigDecimal.ZERO) > 0) {
                applyMoney = applyMoney.add(accountRedeemPO.getApplyMoney()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        return applyMoney;
    }

    @Override
    public BigDecimal getAccountTpcfMoney(List<AccountRechargePO> accountRechargePOS) {
        BigDecimal confirmMoney = BigDecimal.ZERO;
        for (AccountRechargePO po : accountRechargePOS) {
            if (po.getRechargeAmount().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            confirmMoney = confirmMoney.add(po.getRechargeAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        return confirmMoney;
    }

    @Override
    public AnalyTpcfTncfWrapperBean handelTncfFromProcessing(AccountInfoPO accountInfoPO) {
        AnalyTpcfTncfWrapperBean analyTpcfTncfWrapperBean = new AnalyTpcfTncfWrapperBean();
        //获取昨日最新的 NAVTime
        AccountRedeemPO queryParam = new AccountRedeemPO();
        queryParam.setAccountId(accountInfoPO.getId());
        queryParam.setTncfStatus(TncfStatusEnum.PROCESSING);
        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.listAccountRedeem(queryParam);
        if (CollectionUtils.isEmpty(accountRedeemPOs)) {
            return analyTpcfTncfWrapperBean;
        }
        AccountFundNavPO accountNavQuery = new AccountFundNavPO();
        accountNavQuery.setAccountId(accountInfoPO.getId());
        accountNavQuery.setNavTime(DateUtils.addDateByDay(DateUtils.now(), -1));
        
        AccountFundNavPO accountNav = assetFundNavService.selectOneByNavTime(accountNavQuery);
        BigDecimal tncfMoney = BigDecimal.ZERO;
        List<AccountRedeemPO> successAccountRedeems = Lists.newArrayList();


        //同时发生普通赎回和全部赎回场景时，要把普通赎回的金额减掉
        //将accountredeem分为非全赎和全赎两个列表
        Iterable<AccountRedeemPO> accountRedeemNotAllRedeemList = Iterables.filter(accountRedeemPOs, new Predicate<AccountRedeemPO>() {
            @Override
            public boolean apply(@Nullable AccountRedeemPO input) {
                return input.getRedeemType() != RedeemTypeEnum.ALLRedeem;
            }
        });
        BigDecimal notAllRedeemMoney = BigDecimal.ZERO;
        //Remark by bjoon 2020-09-18 double withdraw issue
//        for(AccountRedeemPO accountRedeemPO:accountRedeemNotAllRedeemList){
//            notAllRedeemMoney = notAllRedeemMoney.add(accountRedeemPO.getApplyMoney());
//        }

        for (AccountRedeemPO accountRedeemPO : accountRedeemPOs) {
            if(accountRedeemPO.getIsAnnualPerformanceFee().equalsIgnoreCase("N")){ //Added WooiTatt 20210104
            RedeemApplyPO redeemApplyPO = redeemApplyService.queryByRedeemApplyId(accountRedeemPO.getRedeemApplyId());
           /* AHAM OFF *WooiTatt 
            if (CurrencyEnum.SGD == redeemApplyPO.getSourceAccountType()) {
                //申请的新币需要转换成美金
                ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
                exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
                ExchangeRatePO exchangeRatePO = exchangeRateService.queryLastExchangeRate(exchangeRateParam);
                BigDecimal applyMoneyUsd = redeemApplyPO.getSourceApplyMoney().divide(exchangeRatePO.getUsdToSgd(), 6, BigDecimal.ROUND_DOWN);
                accountRedeemPO.setApplyMoney(applyMoneyUsd);
            }*/
            }
            accountRedeemPO.setNavDate(accountNav.getNavTime());

            UserFundNavPO userFundNav = userFundNavService.selectUserGoalLastOne(accountRedeemPO.getAccountId(), accountRedeemPO.getClientId(), accountRedeemPO.getGoalId());
            BigDecimal totalAsset = userFundNav.getTotalAsset();
            log.info("用户:{},投资账号:{},统计前资产明细:{}", accountRedeemPO.getClientId(), accountRedeemPO.getAccountId(), JSON.toJSON(userFundNav));
            BigDecimal totalHasRedeem = accountRedeemService.totalHasRedeemMoney(accountRedeemPO.getAccountId(), accountRedeemPO.getClientId(), accountRedeemPO.getGoalId());
            log.info("用户:{},投资账号:{},已申请金额:{},现申请金额:{}", accountRedeemPO.getClientId(), accountRedeemPO.getAccountId(), totalHasRedeem, accountRedeemPO.getApplyMoney());
//            BigDecimal residue = totalAsset.subtract(totalHasRedeem);
            if (accountRedeemPO.getRedeemType() == RedeemTypeEnum.ALLRedeem) {
                //Added by bjoon 2020-09-18 double withdraw issue
                 notAllRedeemMoney = BigDecimal.ZERO;
                for(AccountRedeemPO accountRedeemPO2:accountRedeemNotAllRedeemList){
                    if(accountRedeemPO2.getGoalId().equals(accountRedeemPO.getGoalId()))
                        notAllRedeemMoney = notAllRedeemMoney.add(accountRedeemPO2.getApplyMoney());
                }
                //End added by bjoon 2020-09-18 double withdraw issue
                BigDecimal needApplyMoney = totalAsset.subtract(notAllRedeemMoney);
                accountRedeemPO.setApplyMoney(needApplyMoney);
            }
            accountRedeemPO.setTncfStatus(TncfStatusEnum.TNCF);
            accountRedeemPO.setTncfTime(DateUtils.now());
            accountRedeemService.updateById(accountRedeemPO);

            tncfMoney = tncfMoney.add(accountRedeemPO.getApplyMoney());
            successAccountRedeems.add(accountRedeemPO);
        }
        analyTpcfTncfWrapperBean.setTncf(tncfMoney);
        analyTpcfTncfWrapperBean.setAccountRedeemPOs(successAccountRedeems);
        return analyTpcfTncfWrapperBean;
    }

    @Override
    public BigDecimal getUserTpcfMoney(AccountUserPO accountUserPO) {
        AccountRechargePO po = new AccountRechargePO();
        po.setAccountId(accountUserPO.getAccountId());
        //po.setTpcfStatus(TpcfStatusEnum.TPCF);
        po.setTpcfStatus(TpcfStatusEnum.ASSETBUYCOMPLETE);
        po.setClientId(accountUserPO.getClientId());
        po.setGoalId(accountUserPO.getGoalId());
        List<AccountRechargePO> userRechargePOs = accountRechargeService.listByAccountId(po);
        BigDecimal userTpcf = BigDecimal.ZERO;
        for (AccountRechargePO userRechargePO : userRechargePOs) {
            userTpcf = userTpcf.add(userRechargePO.getRechargeAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        log.info("accountId:{},clientId:{},userTpcf:{},goalId:{}", accountUserPO.getAccountId(), accountUserPO.getClientId(), userTpcf, accountUserPO.getGoalId());
        return userTpcf;
    }

    @Override
    public List<AccountRedeemPO> getAccountTncf(Long accountId) {
        AccountRedeemPO queryParam = new AccountRedeemPO();
        queryParam.setAccountId(accountId);
        queryParam.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
        //queryParam.setTncfStatus(TncfStatusEnum.TNCF);
        queryParam.setTncfStatus(TncfStatusEnum.ASSETSELLCOMPLETE);
        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.getRedeemListByTime(queryParam);
        return accountRedeemPOs;
    }

    @Override
    public List<AccountRedeemPO> getAccountHandlingTncf(Long accountId) {
        AccountRedeemPO queryParam = new AccountRedeemPO();
        queryParam.setAccountId(accountId);
        queryParam.setOrderStatus(RedeemOrderStatusEnum.HANDLING);
        queryParam.setTncfStatus(TncfStatusEnum.TNCF);
        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.getRedeemListByTime(queryParam);
        return accountRedeemPOs;
    }

    @Override
    public AnalyTpcfTncfWrapperBean handelTpcfFromProcessing(AccountInfoPO accountInfoPO) {
        AccountRechargePO po = new AccountRechargePO();
        po.setAccountId(accountInfoPO.getId());
        po.setTpcfStatus(TpcfStatusEnum.PROCESSING);
        List<AccountRechargePO> accountRechargePOS = accountRechargeService.listByAccountId(po);
        BigDecimal tpcf = BigDecimal.ZERO;
        for (AccountRechargePO accountRechargePO : accountRechargePOS) {
            accountRechargePO.setTpcfStatus(TpcfStatusEnum.TPCF);
            accountRechargePO.setTpcfTime(DateUtils.now());
            //调用执行模块成功,记录UnBuy处理中资产
            AccountAssetPO accountAsset = accountAssetService.genUnBuyHoldAccountAssetInfo(accountRechargePO);
            analysisSupportService.rechargeToTpcfAddAssets(accountRechargePO, accountAsset);

            tpcf = tpcf.add(accountRechargePO.getRechargeAmount());
        }
        AnalyTpcfTncfWrapperBean tpcfTncfWrapperBean = new AnalyTpcfTncfWrapperBean();
        tpcfTncfWrapperBean.setTpcf(tpcf);
        tpcfTncfWrapperBean.setAccountRechargePOS(accountRechargePOS);

        return tpcfTncfWrapperBean;
    }

    @Override
    public BigDecimal getUserTncfMoney(AccountUserPO accountUserPO) {
        AccountRedeemPO queryParam = new AccountRedeemPO();
        queryParam.setAccountId(accountUserPO.getAccountId());
        queryParam.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
        queryParam.setTncfStatus(TncfStatusEnum.TNCF);
        queryParam.setGoalId(accountUserPO.getGoalId());
        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.getRedeemListByTime(queryParam);
        BigDecimal tncf = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeemPO : accountRedeemPOs) {
            tncf = tncf.add(accountRedeemPO.getApplyMoney());
        }
        return tncf.setScale(6, BigDecimal.ROUND_DOWN);
    }


    @Override
    public BigDecimal getUserTncfShares(AccountUserPO accountUserPO) {
        AccountRedeemPO queryParam = new AccountRedeemPO();
        queryParam.setAccountId(accountUserPO.getAccountId());
        queryParam.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
        //queryParam.setTncfStatus(TncfStatusEnum.TNCF);
        queryParam.setTncfStatus(TncfStatusEnum.ASSETSELLCOMPLETE);
        queryParam.setGoalId(accountUserPO.getGoalId());
        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.getRedeemListByTime(queryParam);
        BigDecimal tncf = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeemPO : accountRedeemPOs) {
            tncf = tncf.add(accountRedeemPO.getConfirmShares());
        }
        return tncf;
    }
    
    @Override
    public List<AccountRechargePO> getAccountTpcfWithAssetComplete(Long accountId) {
        AccountRechargePO po = new AccountRechargePO();
        po.setAccountId(accountId);
        po.setOrderStatus(RechargeOrderStatusEnum.SUCCESS);
        po.setTpcfStatus(TpcfStatusEnum.ASSETBUYCOMPLETE);
        List<AccountRechargePO> accountRechargePOs = accountRechargeService.listByAccountId(po);
        log.info("accountId:{},tpcfs:{}", accountId, JSON.toJSONString(accountRechargePOs));
        return accountRechargePOs;
    }

}
