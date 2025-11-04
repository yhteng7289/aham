package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Confirm;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade40_TradeConfirmSellOrBuyJob_2", cron = "0 00 14 * * ?", description = "SaxoTrade_确认卖或买单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade40_TradeConfirmSellOrBuyJob implements SimpleJob {
    @Autowired
    private Confirm confirm;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> TradeConfirmSellJob");
        confirm.tradeConfirmSellOrBuy();
        log.info("执行结束 =======>>> TradeConfirmSellJob");
    }
}*/