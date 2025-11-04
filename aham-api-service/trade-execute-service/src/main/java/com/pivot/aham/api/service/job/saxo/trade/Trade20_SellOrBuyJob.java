package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Trade;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

//@ElasticJobConf(name = "Trade20_SellOrBuyJob_1", cron = "0 00 10 * * ?", description = "SaxoTrade_执行卖或买单", eventTraceRdbDataSource = "dataSource")
@ElasticJobConf(name = "Trade20_SellOrBuyJob_2", cron = "0 00 13 * * ?", description = "SaxoTrade_执行卖或买单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade20_SellOrBuyJob implements SimpleJob {

    @Autowired
    private Trade trade;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> SellJob");
        trade.sellOrBuy();
        log.info("执行结束 =======>>> SellJob");
    }
}