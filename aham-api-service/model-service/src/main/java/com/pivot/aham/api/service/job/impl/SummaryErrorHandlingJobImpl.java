package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.server.remoteservice.PivotErrorDetailRemoteService;
import com.pivot.aham.api.service.job.SummaryErrorHandlingJob;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;


//@ElasticJobConf(name = "SummaryErrorHandlingJob",
//        cron = "0 0 18 * * ?",
//        shardingItemParameters = "0=1",
//        shardingTotalCount = 1,
//        description = "pivot#同步errorHandling",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class SummaryErrorHandlingJobImpl implements SimpleJob, SummaryErrorHandlingJob {

    @Resource
    private PivotErrorDetailRemoteService pivotErrorDetailRemoteService;

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            log.info("====同步errorHandling,date:{}====", DateUtils.getDate());
            summaryErrorHandlingDetail(DateUtils.now());
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            log.error("====同步errorHandling,date:{}, ex", DateUtils.getDate(), e);
        }
        log.info("====同步PortLevel结束,date:{}====", DateUtils.getDate());
    }

    @Override
    public void summaryErrorHandlingDetail(Date now) {
         pivotErrorDetailRemoteService.summaryErrorHandlingDetail(now);
    }
}
