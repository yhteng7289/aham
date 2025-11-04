package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Revise;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade100_RevisitCancelBuyJob_1", cron = "0 45 15 * * ?", description = "SaxoTrade_取消买单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade100_RevisitCancelBuyJob implements SimpleJob {

    @Autowired
    private Revise revise;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> RevisitCancelBuyJob");
        revise.reviseCancel();
        log.info("执行结束 =======>>> RevisitCancelBuyJob");
    }
}*/