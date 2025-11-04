package com.pivot.aham.api.service.scheduler.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.PersistJobDataAfterExecution;

/**
 * 阻塞任务
 *
 * @author addison
 * @since 2018年11月16日
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatefulJob extends BaseJob implements Job {
}
