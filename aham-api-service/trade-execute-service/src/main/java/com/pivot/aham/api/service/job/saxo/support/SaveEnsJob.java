package com.pivot.aham.api.service.job.saxo.support;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.TradingSupportService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "SaveEnsJob_2", cron = "0 0/15 * * * ?", description = "SaxoSupport_获取充值event", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class SaveEnsJob implements SimpleJob {

    @Autowired
    private TradingSupportService tradingSupportService;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> SaveEnsJob");

        try {
            tradingSupportService.saveAccountFoundingEvent();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("执行结束 =======>>> SaveEnsJob");
    }
}*/
