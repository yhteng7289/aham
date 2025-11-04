package com.pivot.aham.common.core.elasticjob.check;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

public class JobDelayTool {

    public static Date getNextValidTime(Date startDate, String cron) throws ParseException {
        CronExpression cronExpression = new CronExpression(cron);
        Date fireDate = cronExpression.getNextValidTimeAfter(startDate);
        return fireDate;
    }

}
