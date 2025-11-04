package com.pivot.aham.api.service.job.elasticjobdemo;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * name规则：以下划线分隔，第一部分未jobname，第二部分为时区枚举关键字
 * cron:时间表达式
 * shardingItemParameters：分片规则
 * shardingTotalCount:分片总数
 * description:任务描述
 * eventTraceRdbDataSource：作业事件追踪的数据源，elasticjob会新建两张表（job_execution_log，job_status_trace_log）
 */
//@ElasticJobConf(name = "MySimpleJob_1",
//		cron = "* 0/5 * * * ?",
//		failover = true,
//		shardingItemParameters = "0=0,1=1,2=2,3=3",
//		shardingTotalCount=4,description = "简单ce任务")
@Slf4j
public class MyElasticSimpleJob implements SimpleJob {

	@Override
	public void execute(ShardingContext context) {
//		System.out.println(2/0);
		String shardParamter = context.getShardingParameter();
//		log.info("分片参数："+shardParamter);
		int value = Integer.parseInt(shardParamter);
		for (int i = 0; i < 100; i++) {
			if (i % 2 == value) {
				String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
//				log.info("分片:"+shardParamter+","+time + ":开始执行简单任务" + i);
			}
		}
	}

}