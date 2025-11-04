package com.pivot.aham.api.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.job.wrapperbean.UserGoalProfitWrapper;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.enums.analysis.SaxoOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeTypeEnum;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Created by luyang.li on 18/12/17.
 */
@ElasticJobConf(name = "UserProfitJob_2",
        cron = "0 30 20 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易分析#计算用户收益信息", eventTraceRdbDataSource = "dataSource")
@Slf4j
@Component
public class UserProfitJobImpl implements SimpleJob {
    @Autowired
    private AccountRedeemService accountRedeemService;
    @Autowired
    private RedeemApplyService redeemApplyService;
    @Resource
    private AccountUserService accountUserService;
    @Resource
    private UserFundNavService userFundNavService;
    @Resource
    private ExchangeRateService exchangeRateService;
    @Resource
    private SaxoAccountOrderService saxoAccountOrderService;
    @Resource
    private UserProfitInfoService userProfitInfoService;
    @Autowired
    private AccountRechargeService accountRechargeService;

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            log.info("==========计算用户收益信息开始=======");
            calculateUserProfit(DateUtils.now());
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            log.info("==========计算用户收益信息异常：", e);
        }
        log.info("==========计算用户收益信息完成=======");
    }

    /**
     * 1.	Total Return: Current Asset Value (USD) – sum of each investment (USD) + sum of each withdraw(USD)
     * 2.	Portfolio Return: Current Asset Value (USD) – sum of each investment (USD) + sum of each withdraw(USD)
     * 3.	FX Impact =0
     * <p>
     * 这里的新币的提现和充值在 saxo_account_order 里查询，新币查询：
     * 1、新币的充值：SaxoOrderTradeTypeEnum：1 + SaxoOrderActionTypeEnum：6
     * 2、新币的提现：SaxoOrderTradeTypeEnum：2 + SaxoOrderActionTypeEnum：7
     *
     * @param userFundNavPO
     * @param accountUserPO
     * @return
     */
    /*private UserGoalProfitWrapper getSgdGoalProfitInfo(UserFundNavPO userFundNavPO, AccountUserPO accountUserPO) {
        UserGoalProfitWrapper wrapper = new UserGoalProfitWrapper();
        ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
        exchangeRateParam.setRateDate(DateUtils.dayStart(DateUtils.now()));
        exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
        ExchangeRatePO exchangeRatePO = exchangeRateService.getExchangeRate(exchangeRateParam);
        //BigDecimal sgdTotalMoney = userFundNavPO.getTotalAsset().multiply(exchangeRatePO.getUsdToSgd()).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal sgdTotalMoney = userFundNavPO.getTotalAsset().multiply(exchangeRatePO.getUsdToSgd()).setScale(6, BigDecimal.ROUND_DOWN); //Edit by WooiTatt
        log.info("userProfit:accountId:{},goalId:{},clientId:{},sgdTotalMoney:{}", accountUserPO.getAccountId(), userFundNavPO.getGoalId(), userFundNavPO.getClientId(), sgdTotalMoney);
        //新币的入金
        SaxoAccountOrderPO sgdRechargeParam = new SaxoAccountOrderPO();
        sgdRechargeParam.setAccountId(accountUserPO.getAccountId());
        sgdRechargeParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        sgdRechargeParam.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
        sgdRechargeParam.setCurrency(CurrencyEnum.SGD);
        sgdRechargeParam.setClientId(accountUserPO.getClientId());
        sgdRechargeParam.setGoalId(accountUserPO.getGoalId());
        BigDecimal sgdRechargeMoney = saxoAccountOrderService.getClientGoalMoney(sgdRechargeParam);
        log.info("userProfit:accountId:{},goalId:{},clientId:{},rechargeMoney:{}", accountUserPO.getAccountId(), userFundNavPO.getGoalId(), userFundNavPO.getClientId(), sgdRechargeMoney);
        //新币的提现
        SaxoAccountOrderPO sgdRedeemParam = new SaxoAccountOrderPO();
        sgdRedeemParam.setAccountId(accountUserPO.getAccountId());
        sgdRedeemParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRedeemParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
        sgdRedeemParam.setActionType(SaxoOrderActionTypeEnum.SAXOTOUOB);
        sgdRedeemParam.setCurrency(CurrencyEnum.SGD);
        sgdRedeemParam.setGoalId(accountUserPO.getGoalId());
        sgdRedeemParam.setClientId(accountUserPO.getClientId());
        BigDecimal sgdRedeemMoney = saxoAccountOrderService.getClientGoalMoney(sgdRedeemParam);
        log.info("userProfit:accountId:{},goalId:{},clientId:{},redeemMoney:{}", accountUserPO.getAccountId(), userFundNavPO.getGoalId(), userFundNavPO.getClientId(), sgdRedeemMoney);
        //BigDecimal totalProfit = sgdTotalMoney.add(sgdRedeemMoney).subtract(sgdRechargeMoney).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalProfit = sgdTotalMoney.add(sgdRedeemMoney).subtract(sgdRechargeMoney).setScale(2, BigDecimal.ROUND_DOWN); //Edit By WooiTatt
        UserGoalProfitWrapper myrWrapper = getMyrGoalProfitInfo(userFundNavPO);
        BigDecimal fixImpact = totalProfit.subtract(myrWrapper.getPortfolioProfit()).setScale(2, BigDecimal.ROUND_HALF_UP);

        wrapper.setTotalProfit(totalProfit);
        wrapper.setPortfolioProfit(myrWrapper.getPortfolioProfit());
        wrapper.setFxImpact(fixImpact);
        log.info("userProfit:accountId:{},goalId:{},clientId:{},wrapper:{}", accountUserPO.getAccountId(), userFundNavPO.getGoalId(), userFundNavPO.getClientId(), JSON.toJSONString(wrapper));
        return wrapper;
    }*/

    /**
     * 1.	Total Return: Current Asset Value (SGD) – sum of each investment (SGD) + sum of each withdraw(SGD)
     * 2.	Portfolio Return: Current Asset Value (USD) – sum of each investment (USD) + sum of each withdraw(USD)
     * 3.	FX Impact = Total Return - Portfolio Return
     * <p>
     * 这里的美金的提现和充值在 saxo_account_order 里查询，美金查询：
     * 1、美金的充值：SaxoOrderTradeTypeEnum：1 + SaxoOrderActionTypeEnum：4
     * 2、美金的提现：SaxoOrderTradeTypeEnum：2 + SaxoOrderActionTypeEnum：1
     *
     * @param userAssetPO
     * @return
     */
    private UserGoalProfitWrapper getMyrGoalProfitInfo(UserFundNavPO userFundNavPO, AccountUserPO accountUserPO) {
        //Total Withdrawal  (MYR)
        /*SaxoAccountOrderPO myrRedeemParam = new SaxoAccountOrderPO();
        myrRedeemParam.setAccountId(userAssetPO.getAccountId());
        myrRedeemParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        myrRedeemParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
        myrRedeemParam.setActionType(SaxoOrderActionTypeEnum.REDEEM_EXCHANGE);
        myrRedeemParam.setCurrency(CurrencyEnum.MYR);
        myrRedeemParam.setClientId(userAssetPO.getClientId());
        myrRedeemParam.setGoalId(userAssetPO.getGoalId());
        BigDecimal redeemMoney = saxoAccountOrderService.getClientGoalMoney(myrRedeemParam);*/
        AccountRechargePO accountRechargePO = new AccountRechargePO();
        accountRechargePO.setClientId(accountUserPO.getClientId());
        accountRechargePO.setGoalId(accountUserPO.getGoalId());
        accountRechargePO.setTpcfStatus(TpcfStatusEnum.SUCCESS);
        List<AccountRechargePO> listAccountRecharge = accountRechargeService.listAccountRecharge(accountRechargePO);
        BigDecimal totalRecharge = BigDecimal.ZERO;
        for(AccountRechargePO accRechargePO: listAccountRecharge){
            totalRecharge = totalRecharge.add(accRechargePO.getRechargeAmount());
        }
        //Total Deposit (MYR)
        /*SaxoAccountOrderPO myrRechargeParam = new SaxoAccountOrderPO();
        myrRechargeParam.setAccountId(userAssetPO.getAccountId());
        myrRechargeParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        myrRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        myrRechargeParam.setActionType(SaxoOrderActionTypeEnum.RECHARGE_EXCHANGE);
        myrRechargeParam.setCurrency(CurrencyEnum.MYR);
        myrRechargeParam.setGoalId(userAssetPO.getGoalId());
        myrRechargeParam.setClientId(userAssetPO.getClientId());
        BigDecimal rechargeMoney = saxoAccountOrderService.getClientGoalMoney(myrRechargeParam);*/
        AccountRedeemPO accountRedeemPO = new AccountRedeemPO();
        accountRedeemPO.setClientId(accountUserPO.getClientId());
        accountRedeemPO.setGoalId(accountUserPO.getGoalId());
        accountRedeemPO.setTncfStatus(TncfStatusEnum.SUCCESS);
        List<AccountRedeemPO> listAccountRedeem = accountRedeemService.listAccountRedeem(accountRedeemPO);
        BigDecimal totalRedeem =  BigDecimal.ZERO;
        for(AccountRedeemPO accRedeemPO: listAccountRedeem){
            totalRedeem = totalRedeem.add(accRedeemPO.getConfirmMoney());
        }
        
        BigDecimal profit = userFundNavPO.getTotalAsset().add(totalRedeem).subtract(totalRecharge).setScale(2, BigDecimal.ROUND_HALF_UP);

        UserGoalProfitWrapper wrapper = new UserGoalProfitWrapper();
        wrapper.setTotalProfit(profit);
        wrapper.setPortfolioProfit(profit);
        //wrapper.setFxImpact(profit.subtract(profit).setScale(6, BigDecimal.ROUND_HALF_UP));
        return wrapper;
    }

    private void calculateUserProfit(Date date) {
        //1、查新当天的UserFundNAv
        UserFundNavPO userFundNavParam = new UserFundNavPO();
        userFundNavParam.setNavTime(DateUtils.dayStart(date));
        List<UserFundNavPO> userFundNavPOS = userFundNavService.listUserFundNav(userFundNavParam);
        log.info("计算用户收益信息,userFundNavPOS:{}", JSON.toJSONString(userFundNavPOS));
        //2、计算用户收益
        for (UserFundNavPO userFundNavPO : userFundNavPOS) {
            AccountUserPO accountUserParam = new AccountUserPO();
            accountUserParam.setAccountId(userFundNavPO.getAccountId());
            accountUserParam.setClientId(userFundNavPO.getClientId());
            accountUserParam.setGoalId(userFundNavPO.getGoalId());
            AccountUserPO accountUserPO = accountUserService.queryAccountUser(accountUserParam);
            UserGoalProfitWrapper wrapper = null;
            
            wrapper = getMyrGoalProfitInfo(userFundNavPO, accountUserPO);
            /*if (CurrencyEnum.MYR == accountUserPO.getFirstRechargeCurrency()) {
                //美金的处理profit收益公式
//                wrapper = getUsdGoalProfitInfo(userFundNavPO);
                wrapper = getSgdGoalProfitInfo(userFundNavPO, accountUserPO);
            } else {
                //新币的公式
                wrapper = getSgdGoalProfitInfo(userFundNavPO, accountUserPO);
            }*/
            UserProfitInfoPO userProfitInfoPO = new UserProfitInfoPO();
            userProfitInfoPO.setId(Sequence.next());
            userProfitInfoPO.setAccountId(userFundNavPO.getAccountId());
            userProfitInfoPO.setClientId(userFundNavPO.getClientId());
            userProfitInfoPO.setGoalId(userFundNavPO.getGoalId());
            userProfitInfoPO.setProfitDate(DateUtils.dayStart(date));
            userProfitInfoPO.setCreateTime(DateUtils.now());
            userProfitInfoPO.setUpdateTime(DateUtils.now());
            userProfitInfoPO.setTotalProfit(wrapper.getTotalProfit());
            userProfitInfoPO.setPortfolioProfit(wrapper.getPortfolioProfit());
            userProfitInfoPO.setFxImpact(BigDecimal.ZERO);
            userProfitInfoService.saveOrUpdateUserProfit(userProfitInfoPO);
        }
    }

    public void calculaterUserProfit(Date date) {
        calculateUserProfit(date);
    }
}
