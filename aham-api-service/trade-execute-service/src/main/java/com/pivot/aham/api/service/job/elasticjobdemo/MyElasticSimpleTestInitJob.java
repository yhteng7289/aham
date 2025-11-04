package com.pivot.aham.api.service.job.elasticjobdemo;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;

/**
 * name规则：以下划线分隔，第一部分未jobname，第二部分为时区枚举关键字
 * cron:时间表达式
 * shardingItemParameters：分片规则
 * shardingTotalCount:分片总数
 * description:任务描述
 * eventTraceRdbDataSource：作业事件追踪的数据源，elasticjob会新建两张表（job_execution_log，job_status_trace_log）
 */
//@ElasticJobConf(name = "MyElasticSimpleTestInitJob_1",
//        cron = "0/15 * * * * ?",
//        failover = false,
//        shardingItemParameters = "0=0,1=1,2=2,3=3",
//        shardingTotalCount = 4, description = "简单ce任务",
//        listener = "MyElasticSimpleTestInitListener"
//)
@Slf4j
public class MyElasticSimpleTestInitJob implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {

    }

}