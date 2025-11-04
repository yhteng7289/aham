package com.pivot.aham.api.service.job.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.service.job.TradeAnalysisJob;
import com.pivot.aham.api.service.job.TradeAnalysisStrategy;
import com.pivot.aham.api.service.job.impl.rebalance.*;
import com.pivot.aham.api.service.job.wrapperbean.AnalyTpcfTncfWrapperBean;
import com.pivot.aham.api.service.job.interevent.StaticFortradeAnalysisEvent;
import com.pivot.aham.api.service.job.interevent.UserGoalCashFlowEvent;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.PoolingEnum;
import com.pivot.aham.common.enums.analysis.BalStatusEnum;
import com.pivot.aham.common.enums.analysis.BalTradeTypeEnum;
import com.pivot.aham.common.enums.analysis.InitDayEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author luyang.li
 * @date 18/12/17
 * <p>
 * 分析充值和提现流水 --> 走买还是卖
 */
@ElasticJobConf(name = "TradeAnalysisJob_2",
        //cron = "0 0 18 * * ?",
        cron = "0 00 12 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易04_交易分析#当日交易分析下单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class TradeAnalysisJobImpl implements SimpleJob, TradeAnalysisJob {

    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private TradeAnalysisStrategy tradeAnalysisStrategy;
    @Resource
    private AccountBalanceHisRecordService accountBalanceHisRecordService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;
    @Resource
    private AdjustPlanSellBuilder adjustPlanSellBuilder;
    @Resource
    private AccountBalanceExecute accountBalanceExecute;
    @Resource
    private EventBus eventBus;
    @Resource
    private AccountAssetService accountAssetService;
    @Resource
    private TpcfTncfService tpcfTncfService;
    @Resource
    private AccountBalanceRecordService accountBalanceRecordService;
    @Resource
    private AnalysisSupportService analysisSupportService;

    static final String NOTICE_TO_ADD = PropertiesUtil.getString("pivot.error.alert.email");

    /**
     * 遍历所有account 汇总T日所有recharge 汇总T日所有redeem 比较充值和提现，并执行相应策略
     */
    public void tradeAnalysis(String accountId) {
        log.info("=====交易分析下Etf单开始======");
        //1、检查是否开市
        /* AHAM OFF *WooiTatt
        boolean saxoIsTrading = analysisSupportService.checkSaxoIsTranding();
        if (!saxoIsTrading) {
            log.info("===交易分析开始，check SAXO 是否开市为false,不做交易");
            return;
        }*/
        //accountId = Long.valueOf("1364097691240361987");
        String rebalancingAccount = "";
        List<AccountInfoPO> accountInfoPOList = accountInfoService.listAccountInfo();
        for (AccountInfoPO accountInfoPO : accountInfoPOList) {
        	if (!StringUtil.isEmpty(accountId)) {
            	if(!accountInfoPO.getId().equals(Long.parseLong(accountId))) {
            		continue;
            	}
            }
            try {
                //获取昨日ubuy的逻辑必须在tpcf计算之前
                BigDecimal unbuy = getTotalUnbuy(accountInfoPO.getId());
                //处理提现申请为TNCF 并 获取TNCF
                AnalyTpcfTncfWrapperBean tncfWrapperBean = tpcfTncfService.handelTncfFromProcessing(accountInfoPO);
                //处理充值为TPCF 并 获取TPCF
                AnalyTpcfTncfWrapperBean tpcfWrapperBean = tpcfTncfService.handelTpcfFromProcessing(accountInfoPO);
                //调仓逻辑
                String accountInfoPOId = "" + accountInfoPO.getId();
                if (accountInfoPO.getInitDay().equals(InitDayEnum.UN_INIT_DAY)) {
                    if (reBalanceHandler(accountInfoPO, tncfWrapperBean, tpcfWrapperBean)) {
                        rebalancingAccount += accountInfoPOId + ",";
                        continue;
                    }

                }
                //非调仓逻辑
                notRebalanceHandler(accountInfoPO, tncfWrapperBean, tpcfWrapperBean, unbuy);
            } catch (Exception e) {
                log.error("accountid:{},账户交易分析，处理异常", accountInfoPO.getId(), e);
            }
        }
        if (!rebalancingAccount.equalsIgnoreCase("")) {
            rebalancingAccount = rebalancingAccount.substring(0, rebalancingAccount.length() - 1);
            sendMail("Rebalancing Triggered", "Account ID : " + rebalancingAccount + " will run the rebalancing today");
        }
    }

    private void notRebalanceHandler(AccountInfoPO accountInfoPO, AnalyTpcfTncfWrapperBean tncfWrapperBean,
            AnalyTpcfTncfWrapperBean tpcfWrapperBean, BigDecimal unbuy) {
        //记录过程数据
        StaticFortradeAnalysisEvent staticFortradeAnalysisEvent = new StaticFortradeAnalysisEvent();
        staticFortradeAnalysisEvent.setAccountId(accountInfoPO.getId());
        eventBus.post(staticFortradeAnalysisEvent);
        //tpcf,tncf
        BigDecimal tpcf = tpcfWrapperBean.getTpcf();
        BigDecimal tncf = tncfWrapperBean.getTncf();
        log.info("交易分析，查询T-1的，Unbuy:{}", unbuy);
        BigDecimal totalUnbuy = unbuy.add(tpcf);

        List<AccountRedeemPO> accountRedeemPOs = tncfWrapperBean.getAccountRedeemPOs();
        //统计每个goal现金流
        UserGoalCashFlowEvent userGoalCashFlowEvent = new UserGoalCashFlowEvent();
        userGoalCashFlowEvent.setAccountId(accountInfoPO.getId());
        userGoalCashFlowEvent.setAccountRechargePOS(tpcfWrapperBean.getAccountRechargePOS());
        userGoalCashFlowEvent.setAccountRedeemPOs(tncfWrapperBean.getAccountRedeemPOs());
        eventBus.post(userGoalCashFlowEvent);

        log.info("账户:{},所有UnBuy:{},TNCF:{},TPCF:{},totalUnbuy:{}", accountInfoPO.getId(), unbuy, tncf, tpcf, totalUnbuy);
        if (totalUnbuy.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("账户accountId:{},进行策略定制的时候totalUnbuy和提现单为0，不处理", accountInfoPO.getId());
            return;
        }
        //1.tpcf=0 && tncf=0
        if (tpcf.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("账户accountId:{},进行策略定制的时候无充值和提现单不处理,cash调整在NAV计算", accountInfoPO.getId());
            return;
        }
        //2.tcpf>0 && tncf=0
        if (tpcf.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
            tradeAnalysisStrategy.onlyRechargeTradeAnalysis(totalUnbuy, BigDecimal.ZERO, accountInfoPO, Lists.newArrayList());
        }
        //3.tpcf=0 && |tncf|>0
        if (tpcf.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
            tradeAnalysisStrategy.onlyWithdrawaltradeAnalysis(totalUnbuy, tncf, accountInfoPO, accountRedeemPOs);
        }
        //4.tpcf > 0 && tncf < 0
        if (tpcf.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = totalUnbuy.subtract(tncf).setScale(6, BigDecimal.ROUND_HALF_UP);
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                //充值的策略
                tradeAnalysisStrategy.onlyRechargeTradeAnalysis(totalUnbuy, tncf, accountInfoPO, accountRedeemPOs);
            } else if (diff.compareTo(BigDecimal.ZERO) <= 0) {
                //提现的策略
                tradeAnalysisStrategy.onlyWithdrawaltradeAnalysis(totalUnbuy, tncf, accountInfoPO, accountRedeemPOs);
            }
        }
    }

    private BigDecimal getTotalUnbuy(Long accountId) {
        AccountAssetPO accountAssetParam = new AccountAssetPO();
        accountAssetParam.setAccountId(accountId);
        accountAssetParam.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(accountAssetParam);
        return AccountAssetStatistic.getAccountUnbuy(accountAssetPOs);
    }

    /**
     * 调仓逻辑处理
     *
     * @param accountInfoPO
     * @return
     */
    private boolean reBalanceHandler(AccountInfoPO accountInfoPO, AnalyTpcfTncfWrapperBean tncfWrapperBean,
            AnalyTpcfTncfWrapperBean tpcfWrapperBean) {

//        //ab测试
//        boolean abTestSwitch = PropertiesUtil.getBoolean("ab.test.account.switch", false);
//        if (abTestSwitch) {
//            String abTestAccountId = PropertiesUtil.getString("ab.test.account.id");
//            if (abTestAccountId != null && !abTestAccountId.equals(accountInfoPO.getId().toString())) {
//                return false;
//            }
//            log.info("触发白名单执行 ab.test.account.id:{}.", abTestAccountId);
//        }
        //如果有未处理完的调仓，不进行触发
        Long accountId = accountInfoPO.getId();
        AccountBalanceRecord accountBalanceRecord = new AccountBalanceRecord();
        accountBalanceRecord.setAccountId(accountId);
        accountBalanceRecord.setBalStatusList(ImmutableList.of(BalStatusEnum.HANDLING, BalStatusEnum.BUYING, BalStatusEnum.SELLING));
        List<AccountBalanceRecord> accountBalanceRecordList = accountBalanceRecordService.queryAccountBalance(accountBalanceRecord);
        if (!CollectionUtils.isEmpty(accountBalanceRecordList)) {
            return true;
        }
        //检查调仓，生成方案
        AccountBalanceHisRecord accountBalanceHisRecordQuery = new AccountBalanceHisRecord();
        accountBalanceHisRecordQuery.setAccountId(accountInfoPO.getId());
        AccountBalanceHisRecord accountBalanceHisRecord = accountBalanceHisRecordService.selectOne(accountBalanceHisRecordQuery);

        //根据模型标识获取当日目标模型
        ModelRecommendResDTO modelRecommendResDTO = modelServiceRemoteService.getValidRecommendByPortfolioId(accountInfoPO.getPortfolioId());
        //PoolingEnum poolingEnum = modelRecommendResDTO.getPool();
        if(modelRecommendResDTO.getScore().compareTo(BigDecimal.ONE) < 0){
            return false;
        }

        if (accountBalanceHisRecord == null) {
            log.info("插入调仓历史:accountId:{},模型:{}", accountInfoPO.getId(), JSON.toJSONString(modelRecommendResDTO));
            //若没有，插入一条his
            AccountBalanceHisRecord accountBalanceHisRecordUpdate = new AccountBalanceHisRecord();
            accountBalanceHisRecordUpdate.setAccountId(accountInfoPO.getId());
            accountBalanceHisRecordUpdate.setBalId(0L);
            accountBalanceHisRecordUpdate.setLastBalTime(DateUtils.now());
            accountBalanceHisRecordUpdate.setLastProductWeight(modelRecommendResDTO.getProductWeight());
            accountBalanceHisRecordUpdate.setPortfolioScore(modelRecommendResDTO.getScore());
            accountBalanceHisRecordService.updateOrInsert(accountBalanceHisRecordUpdate);
            accountBalanceHisRecord = accountBalanceHisRecordUpdate;
        }

        ReBalanceTriggerContext reBalanceTriggerContext = null;
        /*if (poolingEnum == PoolingEnum.P1) {
            reBalanceTriggerContext = new ReBalanceTriggerContext(new Pool1TriggerStrategy());
        }
        if (poolingEnum == PoolingEnum.P2) {
            reBalanceTriggerContext = new ReBalanceTriggerContext(new Pool2TriggerStrategy());
        }
        if (poolingEnum == PoolingEnum.P3) {
            reBalanceTriggerContext = new ReBalanceTriggerContext(new Pool3TriggerStrategy());
        }*/
        //没有对应处理策略
        reBalanceTriggerContext = new ReBalanceTriggerContext(new PoolAhamTriggerStrategy());
        
        if (reBalanceTriggerContext == null) {
            throw new BusinessException("没有对应的处理策略:" + JSON.toJSONString(modelRecommendResDTO));
        }
        reBalanceTriggerContext.setAccountBalanceHisRecord(accountBalanceHisRecord);
        reBalanceTriggerContext.setModelRecommendResDTO(modelRecommendResDTO);
        ReBalanceTriggerResult reBalanceTriggerResult = reBalanceTriggerContext.executeStrategy();
        log.info("账号{},调仓判断结果{}", accountId, JSON.toJSONString(reBalanceTriggerResult));

        if (reBalanceTriggerResult.getIfAdj()) {
            //调仓逻辑
            adjustPlanSellBuilder.setModelRecommendDTO(modelRecommendResDTO);
            adjustPlanSellBuilder.setAccountInfoPO(accountInfoPO);
            adjustPlanSellBuilder.setTriggerResult(reBalanceTriggerResult);
            List<AccountBalanceAdjDetail> accountBalanceAdjDetailList = adjustPlanSellBuilder.build(tncfWrapperBean, tpcfWrapperBean);
            accountBalanceExecute.setAccountBalanceAdjDetails(accountBalanceAdjDetailList);
            accountBalanceExecute.setAnalyTpcfTncfWrapperBean(tncfWrapperBean);
            accountBalanceExecute.executePlanDetailSell(BalTradeTypeEnum.SELL);

            return true;
        }
        return false;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            tradeAnalysis(null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    public void sendMail(String topic, String body) {
        Email email = new Email();
        email.setBody(body);
        email.setTopic(topic);
        email.setSendTo(NOTICE_TO_ADD);
        email.setSSL(true);
        EmailUtil.sendEmail(email);
    }
}