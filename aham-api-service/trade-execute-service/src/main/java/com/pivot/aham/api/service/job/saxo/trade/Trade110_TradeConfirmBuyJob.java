package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Confirm;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade110_TradeConfirmBuyJob_1", cron = "0 15 16 * * ?", description = "SaxoTrade_确认买单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade110_TradeConfirmBuyJob implements SimpleJob {
    @Autowired
    private Confirm confirm;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> TradeConfirmBuyJob");
        confirm.tradeConfirmBuy();
        log.info("执行结束 =======>>> TradeConfirmBuyJob");
    }
}*/