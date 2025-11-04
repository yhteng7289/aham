//package com.pivot.aham.api.service.core;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.*;
//import com.google.common.eventbus.EventBus;
//import ModelRecommendResDTO;
//import ModelServiceRemoteService;
//import TradeAnalysisStrategy;
//import AccountBalanceExecute;
//import AdjustPlanSellBuilder;
//import Pool1TriggerStrategy;
//import Pool2TriggerStrategy;
//import Pool3TriggerStrategy;
//import ReBalanceTriggerContext;
//import ReBalanceTriggerResult;
//import StaticFortradeAnalysisEvent;
//import UserGoalCashFlowEvent;
//import UserStaticsEvent;
//import AnalyTpcfTncfWrapperBean;
//import com.pivot.aham.api.service.mapper.model.*;
//import com.pivot.aham.api.service.service.*;
//import AccountAssetStatistic;
//import Constants;
//import BusinessException;
//import DateUtils;
//import ErrorLogAndMailUtil;
//import PropertiesUtil;
//import ExchangeRateTypeEnum;
//import PoolingEnum;
//import com.pivot.aham.common.enums.analysis.*;
//import lombok.extern.slf4j.Slf4j;
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.List;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
// * Created by luyang.li on 18/12/17.
// */
//@Slf4j
//@Controller
//@RequestMapping("/test2/")
//public class CronController2 {
//
//    @Resource
//    private AccountInfoService accountInfoService;
//    @Resource
//    private TradeAnalysisStrategy tradeAnalysisStrategy;
//    @Resource
//    private AccountBalanceHisRecordService accountBalanceHisRecordService;
//    @Resource
//    private ModelServiceRemoteService modelServiceRemoteService;
//    @Resource
//    private AdjustPlanSellBuilder adjustPlanSellBuilder;
//    @Resource
//    private AccountBalanceExecute accountBalanceExecute;
//    @Resource
//    private EventBus eventBus;
//    @Resource
//    private AccountAssetService accountAssetService;
//    @Resource
//    private TpcfTncfService tpcfTncfService;
//    @Resource
//    private AccountBalanceRecordService accountBalanceRecordService;
//    @Resource
//    private AnalysisSupportService analysisSupportService;
//
//    @Resource
//    private AccountStaticsService accountStaticsService;
//    @Resource
//    private ExchangeRateService exchangeRateService;
//
//    /**
//     * 遍历所有account 汇总T日所有recharge 汇总T日所有redeem 比较充值和提现，并执行相应策略
//     */
//    public void updateSgd(Long accountId, Date date) {
//        Date calDate = null;
//        if (date == null) {
//            calDate = DateUtils.now();
//        } else {
//            calDate = date;
//        }
//
//        AccountInfoPO accountInfoPO = new AccountInfoPO();
//        List<AccountInfoPO> accountInfoPOList = accountInfoService.queryList(accountInfoPO);
//        for (AccountInfoPO accountInfo : accountInfoPOList) {
//            if (accountId != null && !accountInfo.getId().equals(accountId)) {
//                continue;
//            }
//
//            ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
//            exchangeRateParam.setRateDate(DateUtils.dayStart(calDate));
//            exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
//            ExchangeRatePO exchangeRatePO = exchangeRateService.getExchangeRate(exchangeRateParam);
//            AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
//            accountStaticsQuery.setAccountId(accountInfo.getId());
//            Date yesterDay = DateUtils.addDateByDay(calDate, -1);
//            accountStaticsQuery.setStaticDate(yesterDay);
//            AccountStaticsPO accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);
//
//            if (null != exchangeRatePO && accountStaticsPO != null) {
//                AccountStaticsPO accountStaticsUpdate = new AccountStaticsPO();
//                BigDecimal fxRateUsd = exchangeRatePO.getUsdToSgd();
//                BigDecimal navInSgd = accountStaticsPO.getNavInUsd().multiply(fxRateUsd);
//                BigDecimal adjFundAssetInSgd = accountStaticsPO.getAdjFundShares().multiply(navInSgd);
//                BigDecimal cashWithdrawInSgd = accountStaticsPO.getCashWithdraw().multiply(fxRateUsd);
//
//                accountStaticsUpdate.setId(accountStaticsPO.getId());
//                accountStaticsUpdate.setNavInSgd(navInSgd);
//                accountStaticsUpdate.setAdjFundAssetInSgd(adjFundAssetInSgd);
//                accountStaticsUpdate.setCashWithdrawInSgd(cashWithdrawInSgd);
//                accountStaticsUpdate.setFxRateForClearing(fxRateUsd);
//                accountStaticsUpdate.setFxRateForFundOut(fxRateUsd);
//
//                AccountStaticsPO accountStaticsAfterUpdate = accountStaticsService.updateOrInsert(accountStaticsUpdate);
//
//                UserStaticsEvent userStaticsEvent = new UserStaticsEvent();
//                userStaticsEvent.setAccountStaticsPO(accountStaticsAfterUpdate);
//                eventBus.post(userStaticsEvent);
//
//            } else {
//                log.error("账户{},没有查询到T日的T2汇率或昨日statics为空", accountInfo.getId());
//            }
//
//            if (accountInfo.getInitDay() == InitDayEnum.INIT_DAY) {
//                continue;
//            }
//        }
//
//    }
//
//    public void tradeAnalysis(Long accountId) {
//        //1、检查是否开市
//        boolean saxoIsTrading = true;
//        if (!saxoIsTrading) {
//            log.info("===交易分析开始，check SAXO 是否开市为false,不做交易");
//            return;
//        }
//
//        List<AccountInfoPO> accountInfoPOList = accountInfoService.listAccountInfo();
//        for (AccountInfoPO accountInfoPO : accountInfoPOList) {
//            if (accountId != null && !accountInfoPO.getId().equals(accountId)) {
//                continue;
//            }
//            try {
//                //获取昨日ubuy的逻辑必须在tpcf计算之前
//                BigDecimal unbuy = getTotalUnbuy(accountInfoPO.getId());
//                log.info("accountInfoPO.getId() {} , unbuy {} ", accountInfoPO.getId(), unbuy);
//                //处理提现申请为TNCF 并 获取TNCF
//                AnalyTpcfTncfWrapperBean tncfWrapperBean = tpcfTncfService.handelTncfFromProcessing(accountInfoPO);
//                log.info("Total Negative cfWrapperBean {} ", tncfWrapperBean);
//                //处理充值为TPCF 并 获取TPCF
//                AnalyTpcfTncfWrapperBean tpcfWrapperBean = tpcfTncfService.handelTpcfFromProcessing(accountInfoPO);
//                log.info("Total Positive cfWrapperBean {} ", tpcfWrapperBean);
//                //调仓逻辑
//                if (reBalanceHandler(accountInfoPO, tncfWrapperBean, tpcfWrapperBean)) {
//                    continue;
//                }
//                //非调仓逻辑
//                notRebalanceHandler(accountInfoPO, tncfWrapperBean, tpcfWrapperBean, unbuy);
//            } catch (Exception e) {
//                log.error("accountid:{},账户交易分析，处理异常", accountInfoPO.getId(), e);
//            }
//        }
//    }
//
//    private void notRebalanceHandler(AccountInfoPO accountInfoPO, AnalyTpcfTncfWrapperBean tncfWrapperBean,
//            AnalyTpcfTncfWrapperBean tpcfWrapperBean, BigDecimal unbuy) {
//        //记录过程数据
//        StaticFortradeAnalysisEvent staticFortradeAnalysisEvent = new StaticFortradeAnalysisEvent();
//        staticFortradeAnalysisEvent.setAccountId(accountInfoPO.getId());
//        eventBus.post(staticFortradeAnalysisEvent);
//        //tpcf,tncf
//        BigDecimal tpcf = tpcfWrapperBean.getTpcf();
//        BigDecimal tncf = tncfWrapperBean.getTncf();
//        log.info("交易分析，查询T-1的，Unbuy:{}", unbuy);
//        BigDecimal totalUnbuy = unbuy.add(tpcf);
//
//        List<AccountRedeemPO> accountRedeemPOs = tncfWrapperBean.getAccountRedeemPOs();
//        //统计每个goal现金流
//        UserGoalCashFlowEvent userGoalCashFlowEvent = new UserGoalCashFlowEvent();
//        userGoalCashFlowEvent.setAccountId(accountInfoPO.getId());
//        userGoalCashFlowEvent.setAccountRechargePOS(tpcfWrapperBean.getAccountRechargePOS());
//        userGoalCashFlowEvent.setAccountRedeemPOs(tncfWrapperBean.getAccountRedeemPOs());
//        eventBus.post(userGoalCashFlowEvent);
//
//        log.info("账户:{},所有UnBuy:{},TNCF:{},TPCF:{},totalUnbuy:{}", accountInfoPO.getId(), unbuy, tncf, tpcf, totalUnbuy);
//        if (totalUnbuy.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
//            log.info("账户accountId:{},进行策略定制的时候totalUnbuy和提现单为0，不处理", accountInfoPO.getId());
//            return;
//        }
//        //1.tpcf=0 && tncf=0
//        if (tpcf.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
//            log.info("账户accountId:{},进行策略定制的时候无充值和提现单不处理,cash调整在NAV计算", accountInfoPO.getId());
//            return;
//        }
//        //2.tcpf>0 && tncf=0
//        if (tpcf.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
//            tradeAnalysisStrategy.onlyRechargeTradeAnalysis(totalUnbuy, BigDecimal.ZERO, accountInfoPO, Lists.newArrayList());
//        }
//        //3.tpcf=0 && |tncf|>0
//        if (tpcf.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
//            tradeAnalysisStrategy.onlyWithdrawaltradeAnalysis(totalUnbuy, tncf, accountInfoPO, accountRedeemPOs);
//        }
//        //4.tpcf > 0 && tncf < 0
//        if (tpcf.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
//            BigDecimal diff = totalUnbuy.subtract(tncf).setScale(6, BigDecimal.ROUND_HALF_UP);
//            if (diff.compareTo(BigDecimal.ZERO) > 0) {
//                //充值的策略
//                tradeAnalysisStrategy.onlyRechargeTradeAnalysis(totalUnbuy, tncf, accountInfoPO, accountRedeemPOs);
//            } else if (diff.compareTo(BigDecimal.ZERO) <= 0) {
//                //提现的策略
//                tradeAnalysisStrategy.onlyWithdrawaltradeAnalysis(totalUnbuy, tncf, accountInfoPO, accountRedeemPOs);
//            }
//        }
//    }
//
//    private BigDecimal getTotalUnbuy(Long accountId) {
//        AccountAssetPO accountAssetParam = new AccountAssetPO();
//        accountAssetParam.setAccountId(accountId);
//        accountAssetParam.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
//        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(accountAssetParam);
//        return AccountAssetStatistic.getAccountUnbuy(accountAssetPOs);
//    }
//
//    /**
//     * 调仓逻辑处理
//     *
//     * @param accountInfoPO
//     * @return
//     */
//    private boolean reBalanceHandler(AccountInfoPO accountInfoPO, AnalyTpcfTncfWrapperBean tncfWrapperBean,
//            AnalyTpcfTncfWrapperBean tpcfWrapperBean) {
//
//        //ab测试
//        boolean abTestSwitch = PropertiesUtil.getBoolean("ab.test.account.switch", false);
//        if (abTestSwitch) {
//            String abTestAccountId = PropertiesUtil.getString("ab.test.account.id");
//            if (abTestAccountId != null && !abTestAccountId.equals(accountInfoPO.getId().toString())) {
//                return false;
//            }
//            log.info("触发白名单执行 ab.test.account.id:{}.", abTestAccountId);
//        }
//
//        //如果有未处理完的调仓，不进行触发
//        Long accountId = accountInfoPO.getId();
//        AccountBalanceRecord accountBalanceRecord = new AccountBalanceRecord();
//        accountBalanceRecord.setAccountId(accountId);
//        accountBalanceRecord.setBalStatusList(ImmutableList.of(BalStatusEnum.HANDLING, BalStatusEnum.BUYING, BalStatusEnum.SELLING));
//        List<AccountBalanceRecord> accountBalanceRecordList = accountBalanceRecordService.queryAccountBalance(accountBalanceRecord);
//        log.info("accountBalanceRecordList {} ", accountBalanceRecordList);
//        if (!org.springframework.util.CollectionUtils.isEmpty(accountBalanceRecordList)) {
//            return true;
//        }
//        //检查调仓，生成方案
//        AccountBalanceHisRecord accountBalanceHisRecordQuery = new AccountBalanceHisRecord();
//        accountBalanceHisRecordQuery.setAccountId(accountInfoPO.getId());
//        AccountBalanceHisRecord accountBalanceHisRecord = accountBalanceHisRecordService.selectOne(accountBalanceHisRecordQuery);
//        log.info("accountBalanceHisRecord {} ", accountBalanceHisRecord);
//        //根据模型标识获取当日目标模型
//        ModelRecommendResDTO modelRecommendResDTO = modelServiceRemoteService.getValidRecommendByPortfolioId(accountInfoPO.getPortfolioId());
//        log.info("modelRecommendResDTO {} ", modelRecommendResDTO);
//        PoolingEnum poolingEnum = modelRecommendResDTO.getPool();
//
//        if (accountBalanceHisRecord == null) {
//            log.info("插入调仓历史:accountId:{},模型:{}", accountInfoPO.getId(), JSON.toJSONString(modelRecommendResDTO));
//            //若没有，插入一条his
//            AccountBalanceHisRecord accountBalanceHisRecordUpdate = new AccountBalanceHisRecord();
//            accountBalanceHisRecordUpdate.setAccountId(accountInfoPO.getId());
//            accountBalanceHisRecordUpdate.setBalId(0L);
//            accountBalanceHisRecordUpdate.setLastBalTime(DateUtils.now());
//            accountBalanceHisRecordUpdate.setLastProductWeight(modelRecommendResDTO.getProductWeight());
//            accountBalanceHisRecordUpdate.setPortfolioScore(modelRecommendResDTO.getScore());
//            accountBalanceHisRecordService.updateOrInsert(accountBalanceHisRecordUpdate);
//            accountBalanceHisRecord = accountBalanceHisRecordUpdate;
//        }
//
//        ReBalanceTriggerContext reBalanceTriggerContext = null;
//        if (poolingEnum == PoolingEnum.P1) {
//            reBalanceTriggerContext = new ReBalanceTriggerContext(new Pool1TriggerStrategy());
//        }
//        if (poolingEnum == PoolingEnum.P2) {
//            reBalanceTriggerContext = new ReBalanceTriggerContext(new Pool2TriggerStrategy());
//        }
//        if (poolingEnum == PoolingEnum.P3) {
//            reBalanceTriggerContext = new ReBalanceTriggerContext(new Pool3TriggerStrategy());
//        }
//        //没有对应处理策略
//        if (reBalanceTriggerContext == null) {
//            throw new BusinessException("没有对应的处理策略:" + JSON.toJSONString(modelRecommendResDTO));
//        }
//        reBalanceTriggerContext.setAccountBalanceHisRecord(accountBalanceHisRecord);
//        reBalanceTriggerContext.setModelRecommendResDTO(modelRecommendResDTO);
//        ReBalanceTriggerResult reBalanceTriggerResult = reBalanceTriggerContext.executeStrategy();
//        log.info("账号{},调仓判断结果{}", accountId, JSON.toJSONString(reBalanceTriggerResult));
//
//        if (reBalanceTriggerResult.getIfAdj()) {
//            //调仓逻辑
//            adjustPlanSellBuilder.setModelRecommendDTO(modelRecommendResDTO);
//            adjustPlanSellBuilder.setAccountInfoPO(accountInfoPO);
//            adjustPlanSellBuilder.setTriggerResult(reBalanceTriggerResult);
//            List<AccountBalanceAdjDetail> accountBalanceAdjDetailList = adjustPlanSellBuilder.build(tncfWrapperBean, tpcfWrapperBean);
//            accountBalanceExecute.setAccountBalanceAdjDetails(accountBalanceAdjDetailList);
//            accountBalanceExecute.setAnalyTpcfTncfWrapperBean(tncfWrapperBean);
//            accountBalanceExecute.executePlanDetail(BalTradeTypeEnum.SELL);
//
//            return true;
//        }
//        return false;
//    }
//
//    @RequestMapping(value = "/rebalance")
//    @ResponseBody
//    public void rebalance() {
//        try {
//            tradeAnalysis(null);
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logError(log, e);
//        }
//    }
//
//}
