package com.pivot.aham.common.core.elasticjob.exception;

import com.dangdang.ddframe.job.executor.handler.JobExceptionHandler;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyElasticJobExceptionHandler implements JobExceptionHandler {

    @Override
    public void handleException(String jobName, Throwable cause) {
        log.error("jobName:{} execute error:{}", jobName, cause);
        ErrorLogAndMailUtil.logErrorForTrade(log, cause);
    }
}
