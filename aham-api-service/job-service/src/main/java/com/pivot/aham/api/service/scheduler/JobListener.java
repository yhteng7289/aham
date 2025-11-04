//package com.pivot.aham.api.service.scheduler;
//
//import com.alibaba.fastjson.JSON;
//import com.pivot.aham.api.service.impl.SchedulerServiceImpl;
//import Constants;
//import Email;
//import EmailUtil;
//import NativeUtil;
//import TaskFireLog;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.quartz.JobDataMap;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//
//import java.sql.Timestamp;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
//import java.util.concurrent.TimeUnit;
//
///**
// * quartz任务监听器
// *
// * @author addison
// * @since 2018年11月15日
// */
//public class JobListener implements org.quartz.JobListener {
//    private static Logger logger = LogManager.getLogger(JobListener.class);
//    @Lazy
//    @Autowired
//    private SchedulerServiceImpl schedulerService;
//    // 线程池
//    private static ExecutorService executorService = new ThreadPoolExecutor(4, 20, 5, TimeUnit.SECONDS,
//    new ArrayBlockingQueue(20), new DiscardOldestPolicy());
//    private static String JOB_LOG = "jobLog";
//
//    @Override
//    public String getName() {
//        return "taskListener";
//    }
//
//    @Override
//    public void jobExecutionVetoed(JobExecutionContext context) {
//    }
//
//    /**
//     * 任务开始
//     * @param context
//     */
//    @Override
//    public void jobToBeExecuted(final JobExecutionContext context) {
//        final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
//        String targetObject = jobDataMap.getString("targetObject");
//        String targetMethod = jobDataMap.getString("targetMethod");
//        logger.info("定时任务开始执行：{}.{}", targetObject, targetMethod);
//        // 保存日志
//        TaskFireLog log = new TaskFireLog();
//        log.setStartTime(context.getFireTime());
//        log.setGroupName(targetObject);
//        log.setTaskName(targetMethod);
//        log.setStatus(Constants.JOBSTATE.INIT_STATS);
//        log.setServerHost(NativeUtil.getHostName());
////        log.setServerDuid(NativeUtil.getDUID());
//        schedulerService.updateLog(log);
//        jobDataMap.put(JOB_LOG, log);
//    }
//
//    /**
//     * 任务结束
//     * @param context
//     * @param exp
//     */
//    @Override
//    public void jobWasExecuted(final JobExecutionContext context, JobExecutionException exp) {
//        Timestamp end = new Timestamp(System.currentTimeMillis());
//        final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
//        String targetObject = jobDataMap.getString("targetObject");
//        String targetMethod = jobDataMap.getString("targetMethod");
//        logger.info("定时任务执行结束：{}.{}", targetObject, targetMethod);
//        // 更新任务执行状态
//        final TaskFireLog log = (TaskFireLog) jobDataMap.get(JOB_LOG);
//        if (log != null) {
//            log.setEndTime(end);
//            if (exp != null) {
//                logger.error("定时任务失败: [" + targetObject + "." + targetMethod + "]", exp);
//                String contactEmail = jobDataMap.getString("contactEmail");
//                if (StringUtils.isNotBlank(contactEmail)) {
//                    String topic = String.format("调度[%s.%s]发生异常", targetMethod, targetMethod);
//                    Email email = new Email().setSendTo(contactEmail).setTopic(topic).setBody(exp.getMessage());
//                    sendEmail(email);
//                }
//                log.setStatus(Constants.JOBSTATE.ERROR_STATS);
//                log.setFireInfo(exp.getMessage());
//            } else {
//                if (log.getStatus().equals(Constants.JOBSTATE.INIT_STATS)) {
//                    log.setStatus(Constants.JOBSTATE.SUCCESS_STATS);
//                }
//            }
//        }
//
//        //异步更新执行任务执行日志
//        executorService.submit(() -> {
//            if(log != null) {
//                try {
//                    schedulerService.updateLog(log);
//                } catch (Exception e) {
//                    logger.error("Update TaskRunLog cause error. The log object is : " + JSON.toJSONString(log), e);
//                }
//            }
//        });
//    }
//
//    private void sendEmail(final Email email) {
//        //异步发送
//        executorService.submit(() -> {
//            //todo //为了提升性能，预留功能：先入队列，然后从队列中消费发送邮件
//            logger.info("将发送邮件至：" + email.getSendTo());
//            EmailUtil.sendEmail(email);
//        });
//    }
//}
