package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.job.PortLevelJob;
import com.pivot.aham.api.service.service.PortLevelService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * Created by luyang.li on 18/12/28.
 */
//@ElasticJobConf(name = "PortLevelJob_2",
//        cron = "0 40 09 * * ?",
//        shardingItemParameters = "0=1",
//        shardingTotalCount = 1,
//        description = "模型同步#同步PortLevel",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class PortLevelJobImpl implements SimpleJob, PortLevelJob {

    @Resource
    private PortLevelService portLevelService;

    @Override
    public void synchroPortLevel() {
        portLevelService.synchroPortLevel();
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            log.info("====同步PortLevel开始,date:{}====", DateUtils.getDate());
            synchroPortLevel();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            log.error("====同步PortLevel开始,date:{}, ex", DateUtils.getDate(), e);
        }
        log.info("====同步PortLevel结束,date:{}====", DateUtils.getDate());
    }
}
