package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Recalculate;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade60_RecalculateJob_1", cron = "0 00 13 * * ?", description = "SaxoTrade_重新计算", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade60_RecalculateJob implements SimpleJob {

    @Autowired
    private Recalculate recalculate;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> RecalculateJob");
        recalculate.recalculate();
        log.info("执行结束 =======>>> RecalculateJob");
    }
}*/