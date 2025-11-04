package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.MergeOrder;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@ElasticJobConf(name = "Trade10_MergeOrderTypeJob_2", cron = "0 30 12 * * ?", description = "SaxoTrade_合并订单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade10_MergeOrderTypeJob implements SimpleJob {

    @Autowired
    private MergeOrder mergeOrder;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> MergeOrderJob");
        mergeOrder.mergeEtfOrderForOrderType(true, false);
        log.info("执行结束 =======>>> MergeOrderJob");
    }
}