package com.pivot.aham.common.core.elasticjob.check;

import org.slf4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 监控任务的启动规范Manage
 */
public interface JobDelayManage {

    /**
     * 用于快速定位JobDelayMessage
     */
    Map<String, JobDelayMessage> getDelayMapInner();

    /**
     * JobDelayMessage存储容器
     */
    Set<JobDelayMessage> getJobDelayMessageList();

    /**
     * job名称集合
     */
    Set<String> listjobName();

    /**
     * 根据job名称得到JobDelayMessage
     */
    JobDelayMessage getJobDelayMessage(final String jobName);

    /**
     * 日志注入
     */
    Logger getLog();

    /**
     * 告警消息注入
     */
    MessageSend getMessageSend();

    /**
     * 永久注册任务执行状态变更
     */
    boolean registerWatcherNodeChangedForever(final String jobName);

    /**
     * 将容器中JobDelayMessage执行状态置为成功
     */
    default void updateJobDelayMessageStatusToTrue(final String jobName) {
        JobDelayMessage jobDelayMessage = getDelayMapInner().get(jobName);
        if (jobDelayMessage != null) {
            //限制节点变更时间
            if (jobDelayMessage.getDelayTime().compareTo(System.currentTimeMillis()) <= 0) {
                getLog().info("date:{} update job:{} status true", new Date(), jobName);
                jobDelayMessage.setInitFlag(true);
            }
        }
    }

    /**
     * 创建所有的JobDelayMessage
     */
    default void getAllJobDelayMessage() {
        Set<String> jobNames = this.listjobName();
        for (String jobName : jobNames) {
            JobDelayMessage jobDelayMessage;
            try {
                jobDelayMessage = this.getJobDelayMessage(jobName);
                if (null == jobDelayMessage) {
                    getLog().error("jobBriefInfo is null jobName:{}.", jobName);
                    continue;
                }
                if (getJobDelayMessageList().add(jobDelayMessage)) {
                    getDelayMapInner().put(jobName, jobDelayMessage);
                }
                this.registerWatcherNodeChangedForever(jobName);
            } catch (Exception e) {
                getLog().error("build job error e:{}.", e);
            }
        }
    }

    /**
     * 启动
     */
    default void init() {
        this.getAllJobDelayMessage();
        JobDelayQueueConsumer jobDelayQueueConsumer = new JobDelayQueueConsumer(getJobDelayMessageList(), getMessageSend());
        Thread jobInitCheckThread = new Thread(jobDelayQueueConsumer);
        jobInitCheckThread.setDaemon(true);
        jobInitCheckThread.start();
    }
}
