package com.pivot.aham.api.service.scheduler.job;

import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * job基类
 *
 * @author addison
 * @since 2018年11月16日
 */
public class BaseJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        long start = System.currentTimeMillis();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String targetObject = jobDataMap.getString("targetObject");
        String targetMethod = jobDataMap.getString("targetMethod");
        try {
            LOGGER.info("定时任务[{}.{}]开始", targetObject, targetMethod);
            Object refer = ApplicationContextHolder.getBean(targetObject);
            refer.getClass().getDeclaredMethod(targetMethod).invoke(refer);
            Double time = (System.currentTimeMillis() - start) / 1000.0;
            LOGGER.info("定时任务[{}.{}]用时：{}s", targetObject, targetMethod, time.toString());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
