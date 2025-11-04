package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Finish;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@ElasticJobConf(name = "Trade130_FinishNotifyJob_2", cron = "0 00 16 * * ?", description = "SaxoTrade_交易回调", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade130_FinishNotifyJob implements SimpleJob {

    @Autowired
    private Finish finish;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> FinishNotifyJob");
        finish.finishNotify();
        log.info("执行结束 =======>>> FinishNotifyJob");
    }
}