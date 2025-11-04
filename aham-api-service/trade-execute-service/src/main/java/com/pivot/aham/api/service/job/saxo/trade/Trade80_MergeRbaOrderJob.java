package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.MergeOrder;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-07-03 10:11
 **/
/*@ElasticJobConf(name = "Trade80_MergeRbaOrderJob_1", cron = "0 15 13 * * ?", description = "SaxoTradeRba_合并订单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade80_MergeRbaOrderJob {//implements SimpleJob {

    @Autowired
    private MergeOrder mergeOrder;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> MergeOrderJob");
        mergeOrder.mergeEtfOrderForOrderType(true, true);
        log.info("执行结束 =======>>> MergeOrderJob");
    }
}*/
