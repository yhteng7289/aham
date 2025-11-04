package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Revise;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade30_RevisitCancelSellOrBuyJob_1", cron = "0 00 12 * * ?", description = "SaxoTrade_取消卖或买单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade30_RevisitCancelSellOrBuyJob implements SimpleJob {
    @Autowired
    private Revise revise;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> RevisitCancelSellJob");
        revise.reviseCancel();
        log.info("执行结束 =======>>> RevisitCancelSellJob");
    }
}*/