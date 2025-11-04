package com.pivot.aham.api.service.job.interevent;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountFundNavPO;
import com.pivot.aham.api.service.mapper.model.AccountStaticsPO;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AccountStaticsService;
import com.pivot.aham.api.service.service.AssetFundNavService;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.api.service.support.AccountAssetStatisticBean;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.analysis.FxRateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计account信息
 *
 * @author addison
 * @since 2019年01月22日
 */
@Service
@Slf4j
public class AccountStaticsListener {
    @Autowired
    private AccountAssetService accountAssetService;
    @Autowired
    private AccountStaticsService accountStaticsService;
    @Autowired
    private AssetFundNavService assetFundNavService;
    @Autowired
    private AssetFundNavService accountNavService;

    /**
     * 用于计算超额现金
     */
    public static final String THREEPRECENT = "0.03";


    /**
     * 计算nav后
     * @param accountStaticsEvent
     */
    @Subscribe
    @AllowConcurrentEvents
    public void staticAccountForNav(CalFundNavEvent accountStaticsEvent){
        
        
                //查询昨日收盘价
        //Date yesterday = DateUtils.addDateByDay(new Date(),-1);
        Date yesterday = DateUtils.addDateByDay(accountStaticsEvent.getDate(),-1);//Edit By WooiTatt
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
        log.info("{},收市价:{}", yesterday, etfClosingPriceMap);
        //查找统计表，如果存在就更新
        //yesterday = DateUtils.addDateByDay(new Date(),-1);
        yesterday = DateUtils.addDateByDay(accountStaticsEvent.getDate(),-1);
        AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
        accountStaticsQuery.setAccountId(accountStaticsEvent.getAccountId());
        accountStaticsQuery.setStaticDate(yesterday);
        AccountStaticsPO accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);

        AccountStaticsPO accountStaticsUpdate = new AccountStaticsPO();

        //统计资产
        AccountAssetPO queryParam = new AccountAssetPO();
        queryParam.setAccountId(accountStaticsEvent.getAccountId());
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
        if (CollectionUtils.isEmpty(accountAssetPOs)) {
            log.info("用户资产统计,该账户没有资产,不做处理。accountId:" + accountStaticsEvent.getAccountId());
            return;
        }
        //查询该账号上的总资产
        List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
        //过滤出持有中的资产
        Map<String, AccountAssetStatisticBean> allHoldProductMap = Maps.newHashMap();
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeens) {
            if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING) {
                allHoldProductMap.put(accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean);
            }
        }
        BigDecimal totalUnBuyCash = BigDecimal.ZERO;
        if (allHoldProductMap.get(Constants.UN_BUY_PRODUCT_CODE) != null) {
            BigDecimal productMoney = allHoldProductMap.get(Constants.UN_BUY_PRODUCT_CODE).getProductMoney();
            totalUnBuyCash = totalUnBuyCash.add(productMoney);
        }

        accountStaticsUpdate.setUnbuyAmount(totalUnBuyCash);
        accountStaticsUpdate.setAccountId(accountStaticsEvent.getAccountId());
        accountStaticsUpdate.setTotalEquity(accountStaticsEvent.getTotalEquity());
        accountStaticsUpdate.setCashHolding(accountStaticsEvent.getCashHolding());
        accountStaticsUpdate.setTotalFundValue(accountStaticsEvent.getTotalFundValue());
        accountStaticsUpdate.setFundShares(accountStaticsEvent.getFundShares());
        accountStaticsUpdate.setNavInUsd(accountStaticsEvent.getNavInUsd());
        accountStaticsUpdate.setCashWithdraw(accountStaticsEvent.getCashWithdraw());
        accountStaticsUpdate.setAdjFundAsset(accountStaticsEvent.getAdjFundAsset());
        accountStaticsUpdate.setAdjFundShares(accountStaticsEvent.getAdjFundShares());
        accountStaticsUpdate.setAdjCashHolding(accountStaticsEvent.getAdjCashHolding());

        if (accountStaticsPO != null) {
            accountStaticsUpdate.setId(accountStaticsPO.getId());
        } else {
            accountStaticsUpdate.setStaticDate(DateUtils.dayStart(yesterday));
        }
        accountStaticsService.updateOrInsert(accountStaticsUpdate);
    }


    /**
     * 入金和提现时候
     * @param staticRateForAccountEvent
     */
    @Subscribe
    @AllowConcurrentEvents
    public void staticRate(StaticRateForAccountEvent staticRateForAccountEvent){
        //查找统计表，如果存在就更新
        AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
        accountStaticsQuery.setAccountId(staticRateForAccountEvent.getAccountId());
        accountStaticsQuery.setStaticDate(new Date());
        AccountStaticsPO accountStaticsPO = null;


        AccountStaticsPO accountStaticsUpdate = new AccountStaticsPO();
        if(staticRateForAccountEvent.getFxRateTypeEnum() == FxRateTypeEnum.FUNDIN){
            accountStaticsQuery.setStaticDate(new Date());
            accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);
            accountStaticsUpdate.setFxRateForFundIn(staticRateForAccountEvent.getFxRate());

        }else if(staticRateForAccountEvent.getFxRateTypeEnum() == FxRateTypeEnum.FUNDOUT){
            Date yesterday = DateUtils.addDateByDay(new Date(),-1);
            accountStaticsQuery.setStaticDate(yesterday);
            accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);

            BigDecimal fxRateUsd = staticRateForAccountEvent.getFxRate();
            BigDecimal navInSgd = accountStaticsPO.getNavInUsd().multiply(fxRateUsd);
            BigDecimal adjFundAssetInSgd = accountStaticsPO.getAdjFundShares().multiply(navInSgd);
            BigDecimal cashWithdrawInSgd = accountStaticsPO.getCashWithdraw().multiply(fxRateUsd);
            accountStaticsUpdate.setNavInSgd(navInSgd);
            accountStaticsUpdate.setAdjFundAssetInSgd(adjFundAssetInSgd);
            accountStaticsUpdate.setCashWithdrawInSgd(cashWithdrawInSgd);
            accountStaticsUpdate.setFxRateForClearing(fxRateUsd);


            accountStaticsUpdate.setFxRateForFundOut(staticRateForAccountEvent.getFxRate());
        }

        accountStaticsUpdate.setAccountId(staticRateForAccountEvent.getAccountId());
        updateStatics(accountStaticsPO, accountStaticsUpdate);
    }


    /**
     * 交易分析前
     * @param staticFortradeAnalysisEvent
     */
    @Subscribe
    @AllowConcurrentEvents
    public void staticForTradeAnalysis(StaticFortradeAnalysisEvent staticFortradeAnalysisEvent){
        log.info("StaticFortradeAnalysisEvent开始:{}", JSON.toJSON(staticFortradeAnalysisEvent));
        //查找统计表，如果存在就更新
        AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
        accountStaticsQuery.setAccountId(staticFortradeAnalysisEvent.getAccountId());
        accountStaticsQuery.setStaticDate(new Date());
        AccountStaticsPO accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);

        //根据navtime获取昨日账户资产
        AccountFundNavPO accountNavQuery = new AccountFundNavPO();
        accountNavQuery.setAccountId(staticFortradeAnalysisEvent.getAccountId());
        accountNavQuery.setNavTime(DateUtils.now());
        AccountFundNavPO accountNav = accountNavService.selectOneByNavTime(accountNavQuery);
        if (accountNav == null) {
            log.error("账户{},找不到对应账户基金净值记录", staticFortradeAnalysisEvent.getAccountId());
            return;
        }

        //请求执行器下提现申请单
        //赎回金额<=(CashExcess_SAXO = ADJ_Cash_SAXO_USD(t-1)-ADJ_Fund_Asset(t-1)*3%)*0.95,直接从saxo现金账户提现
        //计算超额现金=(昨日cash-(昨日总资产*0.03))*0.95
        BigDecimal todayTotalCash = accountNav.getTotalCash();
        BigDecimal todayTotalAsset = accountNav.getTotalAsset();
        BigDecimal totalAssetPre = todayTotalAsset.multiply(new BigDecimal(THREEPRECENT));
        BigDecimal cashExcess = todayTotalCash.subtract(totalAssetPre);
        //查询所etf的收市价格
        Date yesterday = DateUtils.addDateByDay(new Date(),-1);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
        log.info("{},收市价:{}", yesterday, etfClosingPriceMap);
        AccountStaticsPO accountStaticsUpdate = new AccountStaticsPO();
        accountStaticsUpdate.setAccountId(staticFortradeAnalysisEvent.getAccountId());
        accountStaticsUpdate.setExcessCash(cashExcess);

        updateStatics(accountStaticsPO, accountStaticsUpdate);
    }

    /**
     * etf交易回调后
     * @param staticForEtfCallBackEvent
     */
    @Subscribe
    @AllowConcurrentEvents
    public void staticForEtfCallBack(StaticForEtfCallBackEvent staticForEtfCallBackEvent){
        log.info("staticForEtfCallBackEvent开始:{}", JSON.toJSON(staticForEtfCallBackEvent));
        //查找统计表，如果存在就更新
        AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
        accountStaticsQuery.setAccountId(staticForEtfCallBackEvent.getAccountId());
        Date yesterday = DateUtils.addDateByDay(new Date(),-1);
        accountStaticsQuery.setStaticDate(yesterday);
        AccountStaticsPO accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);

        AccountStaticsPO accountStaticsUpdate = new AccountStaticsPO();
        accountStaticsUpdate.setCashResidual(staticForEtfCallBackEvent.getCashResidual());
        accountStaticsUpdate.setCashBySell(staticForEtfCallBackEvent.getCashBySell());
        accountStaticsUpdate.setTransactionCostBuy(staticForEtfCallBackEvent.getTransactionCostBuy());
        accountStaticsUpdate.setTransactionCostSell(staticForEtfCallBackEvent.getTransactionCostSell());

        if (accountStaticsPO != null) {
            accountStaticsUpdate.setId(accountStaticsPO.getId());
        } else {
            accountStaticsUpdate.setStaticDate(DateUtils.dayStart(yesterday));
        }
        accountStaticsUpdate.setAccountId(staticForEtfCallBackEvent.getAccountId());
        accountStaticsService.updateOrInsert(accountStaticsUpdate);
        log.info("staticForEtfCallBackEvent完成:{}", JSON.toJSON(staticForEtfCallBackEvent));

    }


    /**
     * 计算nav前
     * @param reducedNormalFeeEvent
     */
    @Subscribe
    @AllowConcurrentEvents
    public void reducedNormalFee(StaticReducedNormalFeeEvent reducedNormalFeeEvent){
        //查找统计表，如果存在就更新
        AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
        Date yesterday = DateUtils.addDateByDay(new Date(),-1);
        accountStaticsQuery.setAccountId(reducedNormalFeeEvent.getAccountId());
        accountStaticsQuery.setStaticDate(yesterday);
//        accountStaticsQuery.setAccountStaticsStatus(AccountStaticsStatusEnum.HANDLING);
        AccountStaticsPO accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);

        AccountStaticsPO accountStaticsUpdate = new AccountStaticsPO();
        accountStaticsUpdate.setAccountId(reducedNormalFeeEvent.getAccountId());
        accountStaticsUpdate.setMgtFee(reducedNormalFeeEvent.getMgtFee());
        accountStaticsUpdate.setCustFee(reducedNormalFeeEvent.getCustFee());
        accountStaticsUpdate.setGstMgtFee(reducedNormalFeeEvent.getGstMgtFee());

        if (accountStaticsPO != null) {
            accountStaticsUpdate.setId(accountStaticsPO.getId());
        } else {
            accountStaticsUpdate.setStaticDate(DateUtils.dayStart(yesterday));
        }
        accountStaticsService.updateOrInsert(accountStaticsUpdate);
    }


    /**
     * 来分红时
     * @param cashDividendEvent
     */
    @Subscribe
    @AllowConcurrentEvents
    public void cashDividend(CashDividendEvent cashDividendEvent){
        //查找统计表，如果存在就更新
        AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
        accountStaticsQuery.setAccountId(cashDividendEvent.getAccountId());
        accountStaticsQuery.setStaticDate(new Date());
        AccountStaticsPO accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);

        AccountStaticsPO accountStaticsUpdate = new AccountStaticsPO();
        accountStaticsUpdate.setAccountId(cashDividendEvent.getAccountId());
        accountStaticsUpdate.setCashDividend(cashDividendEvent.getCashDividend());

        updateStatics(accountStaticsPO, accountStaticsUpdate);
    }

    private void updateStatics(AccountStaticsPO accountStaticsPO, AccountStaticsPO accountStaticsUpdate) {
        if (accountStaticsPO != null) {
            accountStaticsUpdate.setId(accountStaticsPO.getId());
        } else {
            accountStaticsUpdate.setStaticDate(DateUtils.dayStart(new Date()));
        }
        accountStaticsService.updateOrInsert(accountStaticsUpdate);
    }
}
