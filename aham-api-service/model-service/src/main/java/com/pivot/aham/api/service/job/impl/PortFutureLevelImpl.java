package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.job.PortFutureLevelJob;
import com.pivot.aham.api.service.service.PortFutureLevelService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by luyang.li on 19/2/22.
 */
//@ElasticJobConf(name = "PortFutureLevelJob_2",
//        cron = "0 50 09 * * ?",
//        shardingItemParameters = "0=1",
//        shardingTotalCount = 1,
//        description = "模型同步#同步PortFutureLevel",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class PortFutureLevelImpl implements SimpleJob, PortFutureLevelJob {

    @Resource
    private PortFutureLevelService portFutureLevelService;

    @Override
    public void synchroPortFutureLevel() {
        Date date = DateUtils.now();
        log.info("====模型同步#同步PortFutureLevel开始====");
        portFutureLevelService.synchroPortFutureLevel(date);
        log.info("====模型同步#同步PortFutureLevel结束====");
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            synchroPortFutureLevel();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }
}
