package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Demerge;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade50_DemergeSellOrBuyOrderJob_2", cron = "0 00 15 * * ?", description = "SaxoTrade_拆分卖或买订单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade50_DemergeSellOrBuyOrderJob implements SimpleJob {

    @Autowired
    private Demerge demerge;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> DemergeSellOrderJob");
        demerge._demergeOrderSellOrBuy();
        log.info("执行结束 =======>>> DemergeSellOrderJob");
    }
}*/
