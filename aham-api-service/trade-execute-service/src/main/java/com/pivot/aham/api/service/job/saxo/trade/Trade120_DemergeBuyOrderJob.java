package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.impl.trade.Demerge;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.enums.EtfmergeOrderTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade120_DemergeBuyOrderJob_1", cron = "0 30 16 * * ?", description = "SaxoTrade_拆分买订单", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade120_DemergeBuyOrderJob implements SimpleJob {
    @Autowired
    private Demerge demerge;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> DemergeBuyOrderJob");
        demerge.demergeOrder(EtfmergeOrderTypeEnum.BUY);
        log.info("执行结束 =======>>> DemergeBuyOrderJob");
    }
}*/