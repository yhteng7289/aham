package com.pivot.aham.common.core.elasticjob.check;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
public class JobDelayMessage implements Delayed {

    private String jobName;
    private String cron;
    private boolean initFlag = false;
    private Long delayTime;
    /**
     * 任务检测时机时间偏移量
     */
    private Long offsetTime;

    public JobDelayMessage(String jobName, String cron, long delayTime, long offsetTime) {
        this.jobName = jobName;
        this.delayTime = delayTime;
        this.offsetTime = offsetTime;
        this.cron = cron;
    }

    private final static Long offsetTimePreset = 1 * 1000L;

    public JobDelayMessage(String jobName, String cron, long delayTime) {
        this(jobName, cron, delayTime, offsetTimePreset);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long remaining = delayTime - System.currentTimeMillis() + offsetTime;
        return unit.convert(remaining, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
