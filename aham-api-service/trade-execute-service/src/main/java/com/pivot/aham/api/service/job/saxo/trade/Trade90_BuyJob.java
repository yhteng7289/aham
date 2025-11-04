package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Trade;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.support.generator.Sequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade90_BuyJob_1", cron = "0 30 13 * * ?", description = "SaxoTrade_执行买单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade90_BuyJob implements SimpleJob {

    @Autowired
    private Trade trade;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> BuyJob");
        Long orderId = Sequence.next();
        trade.buy(orderId);
        log.info("执行结束 =======>>> BuyJob");
    }
}*/