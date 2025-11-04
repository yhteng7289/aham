package com.pivot.aham.api.service.job.saxo;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.SaxoStatisticService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "Trade1_ShareDividendJob_2",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        cron = "0 1/30 12-14 * * ?", description = "saxo日终分红记录同步_回调")
@Slf4j
public class Trade1_SaxoShareDividendJob implements SimpleJob {
    @Autowired
    private SaxoStatisticService saxoStatisticService;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("开始执行 =======>>> ShareDividendJob");

        try {
            saxoStatisticService.shareDividEnd(null);
        } catch (Exception e) {
            log.error("读取分红邮件异常,e:{}",e);
            ErrorLogAndMailUtil.logError(log, e);
        }
        log.info("执行结束 =======>>> ShareDividendJob");
    }
}*/
