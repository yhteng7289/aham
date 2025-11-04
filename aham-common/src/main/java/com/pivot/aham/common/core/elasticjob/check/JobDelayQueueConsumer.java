package com.pivot.aham.common.core.elasticjob.check;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.DelayQueue;

/**
 * 线程内的处理和时间存在offset 不做线程安全处理
 * <p>
 * 保持delayQueueInner任务实例只有一个，当前任务实例处理完毕更新任务实例下一个fire时间
 * <p>
 * 邮件发送需异步处理
 */
@Slf4j
public class JobDelayQueueConsumer implements Runnable {

    private final DelayQueue<JobDelayMessage> delayQueueInner;

    private final MessageSend messageSend;

    public JobDelayQueueConsumer(Set<JobDelayMessage> delaySet, MessageSend messageSend) {
        this.delayQueueInner = new DelayQueue<>(delaySet);
        this.messageSend = messageSend;
    }

    @Override
    public void run() {
        if (delayQueueInner.isEmpty()) {
            //中断线程
            throw new RuntimeException("delayQueueInner should not be empty where thread init");
        }
        final Date startDate = new Date();
        log.info("thread start date:{}.", startDate);
        while (true) {
            try {
                JobDelayMessage jobDelayMessage = delayQueueInner.take();
                final boolean initFlag = jobDelayMessage.isInitFlag();
                final long delayTime = jobDelayMessage.getDelayTime();
                this.updateJobDelayMessage(jobDelayMessage);
                if (initFlag || jobDelayMessage.getDelayTime().compareTo(startDate.getTime()) < 0) {
                    continue;
                }
                log.error("jobName:{} init error, should be init at date:{}.", jobDelayMessage.getJobName(), new Date(delayTime));
                try {
                    messageSend.send(jobDelayMessage.getJobName() + " init error, should be init at date:" + new Date(delayTime));
                } catch (Exception e) {
                    log.error("JobDelayQueueConsumer 短信发送失败.");
                }
            } catch (InterruptedException e) {
                log.error("JobDelayQueueConsumer run error InterruptedException.");
            } catch (ParseException p) {
                log.error("JobDelayQueueConsumer run error ParseException.");

            }
        }
    }

    private void updateJobDelayMessage(JobDelayMessage jobDelayMessage) throws ParseException {
        Date nextValidTime = JobDelayTool.getNextValidTime(new Date(), jobDelayMessage.getCron());
        jobDelayMessage.setInitFlag(false);
        jobDelayMessage.setDelayTime(nextValidTime.getTime());
        delayQueueInner.offer(jobDelayMessage);
    }
}
