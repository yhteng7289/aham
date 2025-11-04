package com.pivot.aham.api.service.job;

/**
 * Created by luyang.li on 18/12/17.
 *
 * 分析充值和提现流水 --> 走买还是卖
 */
public interface TradeAnalysisJob {

    void tradeAnalysis(String accountId);
}
