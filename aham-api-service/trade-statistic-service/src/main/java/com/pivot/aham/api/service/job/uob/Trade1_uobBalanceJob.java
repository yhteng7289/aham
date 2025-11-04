package com.pivot.aham.api.service.job.uob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.UobBalanceService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "trade1_uobBalanceJob_2",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        cron = "0 40 12 * * ?", description = "uob对账文件上次Job")
@Slf4j
public class Trade1_uobBalanceJob implements SimpleJob {
    @Autowired
    private UobBalanceService uobBalanceService;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("开始执行 =======>>> uobBalanceJob");

        try {
            uobBalanceService.statisExport();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
        log.info("执行结束 =======>>> uobBalanceJob");
    }
}*/
