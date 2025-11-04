package com.dangdang.ddframe.job.lite.internal.schedule;

import com.dangdang.ddframe.job.exception.JobSystemException;
import com.pivot.aham.common.enums.TimeZoneTypeEnum;
import lombok.RequiredArgsConstructor;
import org.quartz.*;

import java.util.TimeZone;

/**
 * 作业调度控制器.
 * 
 * @author zhangliang
 */
@RequiredArgsConstructor
public final class JobScheduleController {
    
    private final Scheduler scheduler;
    
    private final JobDetail jobDetail;
    
    private final String triggerIdentity;
    
    /**
     * 调度作业.
     * 
     * @param cron CRON表达式
     */
    public void scheduleJob(final String cron) {
        try {
            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.scheduleJob(jobDetail, createTrigger(cron));
            }
            scheduler.start();
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 重新调度作业.
     * 
     * @param cron CRON表达式
     */
    public synchronized void rescheduleJob(final String cron) {
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerIdentity));
            if (!scheduler.isShutdown() && null != trigger && !cron.equals(trigger.getCronExpression())) {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerIdentity), createTrigger(cron));
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    /**
     * elasticjob原来用的trigger没有设置时区。
     *
     * 扩展思路：
     * 在jobname中加入时区因子（对应枚举的具体值）
     *
     */
    private CronTrigger createTrigger(final String cron) {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

        cronScheduleBuilder.withMisfireHandlingInstructionDoNothing();

        String jobName = jobDetail.getKey().getName();
        String[] infos = jobName.split("_");
        //如果长度大于2
        if(infos.length>=2){
            TimeZoneTypeEnum timeZoneTypeEnum = TimeZoneTypeEnum.forValue(Integer.valueOf(infos[infos.length-1]));
            cronScheduleBuilder.inTimeZone(TimeZone.getTimeZone(timeZoneTypeEnum.getDesc()));
        }

        return TriggerBuilder.newTrigger().withIdentity(triggerIdentity).withSchedule(cronScheduleBuilder).build();
    }
    
    /**
     * 判断作业是否暂停.
     * 
     * @return 作业是否暂停
     */
    public synchronized boolean isPaused() {
        try {
            return !scheduler.isShutdown() && Trigger.TriggerState.PAUSED == scheduler.getTriggerState(new TriggerKey(triggerIdentity));
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 暂停作业.
     */
    public synchronized void pauseJob() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.pauseAll();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 恢复作业.
     */
    public synchronized void resumeJob() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.resumeAll();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 立刻启动作业.
     */
    public synchronized void triggerJob() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.triggerJob(jobDetail.getKey());
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 关闭调度器.
     */
    public synchronized void shutdown() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
}