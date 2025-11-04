package com.pivot.aham.api.service.job.saxo;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.SaxoStatisticService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade2_SaxoTotalStatisJob_2",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        cron = "0 0 15 * * ?", description = "saxo日终总资产对账")
@Slf4j
public class Trade2_SaxoTotalStatisJob implements SimpleJob {
    @Autowired
    private SaxoStatisticService saxoStatisticService;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("开始执行 =======>>> SaxoTotalStatisJob");

        try {
            saxoStatisticService.totalStatisEnd(null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
        log.info("执行结束 =======>>> SaxoTotalStatisJob");
    }
}
*/