package com.pivot.aham.common.core.util;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.common.core.support.email.Email;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tonghao on 2018/2/7.
 */
@Slf4j
public class ErrorLogAndMailUtil {
//    private static final Logger logger = LogManager.getLogger(ErrorLogAndMailUtil.class);

    //private static final List<String> DEV_PHONE_NUMBER = Lists.newArrayList("18611694691", "13366348001", "17601002671", "18311078590");
    private static final String MAIL_ADD_TRADING = PropertiesUtil.getString("pivot.error.alert.email");
    static final String DEV_TO_ADD = PropertiesUtil.getString("pivot.error.alert.email");
    static final String NOTICE_TO_ADD = PropertiesUtil.getString("pivot.error.alert.email");

    private static boolean isSendErrorMail(){
        return PropertiesUtil.getBoolean("env.error.log.send");
    }

    private static String getEnvRemark(){
        return PropertiesUtil.getString("email.env.name");
    }

    /**
     * 邮件线程池
     * 采用DiscardOldestPolicy策略，丢掉时间最长的那个任务
     */
    private static ExecutorService executorService = new ThreadPoolExecutor(2, 20, 5, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.DiscardOldestPolicy());

    private static void sendMail(String content) {
        sendMail(content, DEV_TO_ADD);
    }


    private static void sendMail(String content, String sendTo) {
        try {
            Email email = new Email();
            email.setBody(content);
            email.setTopic("ERROR【" + getEnvRemark() + "】");
            email.setSendTo(sendTo);
            email.setSSL(true);
            executorService.execute(() -> EmailUtil.sendEmail(email));

        } catch (Exception e) {
            log.error("发送邮件失败:{}", content, e);
        }
    }


    private static void sendMailNotict(String content) {
        try {
            Email email = new Email();
            email.setBody(content);
            email.setTopic("NOTICE【" + getEnvRemark() + "】");
            email.setSendTo(NOTICE_TO_ADD);
            email.setSSL(true);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    EmailUtil.sendEmail(email);
                }
            });

        } catch (Exception e) {
            log.error("发送邮件失败:{}", content, e);
        }
    }

    public static void logInfo(Logger logger, String content) {
        logger.info(content);
    }

    public static void logInfo(Logger logger, String format, Object... arguments) {
        String content = String.format(format, arguments);
        logger.info(content);
    }

    public static void logError(Logger logger, Object content) {
        logError(logger, content, DEV_TO_ADD);
    }

    public static void logErrorForTrade(Logger logger, Object content) {
        logError(logger, content, MAIL_ADD_TRADING);
    }

    public static void logError(Logger logger, Object content, String sendTo) {
        String msg = "";
        if (content != null) {
            if (content instanceof String) {
                msg = (String) content;
            } else if (content instanceof Exception) {
                msg = ExceptionUtils.getStackTrace((Exception) content);
            } else {
                msg = JSON.toJSONString(content);
            }
        }

        logger.error(msg,content);
        if (isSendErrorMail()) {
            sendMail(msg, sendTo);
            //sendSms(content, true);
        }
    }


    public static void logNotice(Logger logger, Object content) {
        String msg = "";
        if (content != null) {
            if (content instanceof String) {
                msg = (String) content;
            } else if (content instanceof Exception) {
                msg = ExceptionUtils.getStackTrace((Exception) content);
            } else {
                msg = JSON.toJSONString(content);
            }
        }

        logger.info(msg,content);
        if (isSendErrorMail()) {
            sendMailNotict(msg);
            //sendSms(content, true);
        }
    }
    public static void logErrorWithoutSms(Logger logger, String content) {
        logger.error(content);
        if (isSendErrorMail()) {
            sendMail(content);
        }
    }

    public static void logErrorByFormat(Logger logger, String format, Object... arguments) {
        String content = MessageFormatter.arrayFormat(format, arguments).getMessage();
        logger.error(content);
        if (isSendErrorMail()) {
            sendMail(content);
            //sendSms(content, true);
        }
    }

    public static void logErrorWithoutSmsByFormat(Logger logger, String format, Object... arguments) {
        String content = MessageFormatter.arrayFormat(format, arguments).getMessage();
        logger.error(content);
        if (isSendErrorMail()) {
            sendMail(content);
        }
    }

//    public static void sendSms(String content, boolean isError) {
//        try {
//            if (WebConfig.isSendErrorSms()) {
//                String str;
//                if (content.length() > 220) {
//                    str = content.substring(0, 210);
//                } else {
//                    str = content;
//                }
//
//                String msg;
//                if (isError) {
//                    msg = WebConfig.getSmsSuffix() + "[" + WebConfig.getEnvRemark() + "]催收报错,内容已发邮件,请检查!! " + str;
//                } else {
//                    msg = WebConfig.getSmsSuffix() + "[" + WebConfig.getEnvRemark() + "]催收通知, " + str;
//                }
//                List<Notify> notifyList = Lists.newArrayList();
//                for (String mobile : DEV_PHONE_NUMBER) {
//                    Notify notify = new Notify();
//                    notify.setSendTo(mobile);
//                    notify.setContentType(1);
//                    notify.setContent(msg);
//                    notifyList.add(notify);
//                }
//
//                SMSClient.sendNotifies2Queue(notifyList, SmsHelper.getSmsSite());
//            }
//        } catch (Exception e) {
//            logger.error(ExceptionUtils.getStackTrace(e));
//        }
//    }
}
