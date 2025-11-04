package com.pivot.aham.api.web.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.web.app.service.AppRequestService;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.util.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author YYYz
 */
@Service
public class AppRequestServiceImpl implements AppRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppRequestServiceImpl.class);

    private static final String EMAIL_TITLE_TEM = "【{0}】环境 app 请求路径:{1} 发送未知异常";

    /**
     * 基金接口地址
     */
    private static final String PIVOT_SERVICE_URL = PropertiesUtil.getString("PIVOT_SERVICE_URL");
    /**
     * 文件日志模板
     */
    private static final String LOG_MSG_TEM = "pivotInterface,requestAddress:{0}##request:{1}##response:{2}##exceptionMessage:{3}";

    private static final String FAIL_MSG_TEM = "pivotInterface,requestAddress:{0}##request:{1}##response:{2}##异常exceptionMessage信息:{3}";

    private static final String ERROR_EMAIL_ADDRESS = PropertiesUtil.getString("ERROR_EMAIL_ADDRESS");

    @Override
    public HttpResMsg callAppApi(String param, String apiUrl) {
        HttpResMsg resMsg = null;
        String activeProfile = ApplicationContextHolder.getActiveProfile();
        try {
            LOGGER.info("PIVOT_SERVICE_URL : " + PIVOT_SERVICE_URL);
            resMsg = HttpclientUtils.post(PIVOT_SERVICE_URL + apiUrl, param);
            if (!resMsg.isSuccess()) {
                //请求返回错误
                String topic = MessageFormat.format(EMAIL_TITLE_TEM, activeProfile, apiUrl);
                Email email = new Email()
                        .setTemplateName("ErrorEmail")
                        .setTemplateVariables(InstanceUtil.newHashMap("exMsg", JSON.toJSONString(resMsg)))
                        .setSendTo(ERROR_EMAIL_ADDRESS)
                        .setTopic(topic);
                EmailUtil.sendEmail(email);
                //log
                String errorLog = MessageFormat.format(FAIL_MSG_TEM, apiUrl, param, resMsg, "无");
                LOGGER.info(errorLog);
                return resMsg;
            }
        } catch (Exception e) {
            //未知异常，预留邮件告警
            String topic = MessageFormat.format(EMAIL_TITLE_TEM, activeProfile, apiUrl);
            Email email = new Email()
                    .setTemplateName("ExceptionEmail")
                    .setTemplateVariables(InstanceUtil.newHashMap("exMsg", ExceptionUtils.getFullStackTrace(e)))
                    .setSendTo(ERROR_EMAIL_ADDRESS)
                    .setTopic(topic);
            EmailUtil.sendEmail(email);
        }
        //log
        String apiLog = MessageFormat.format(LOG_MSG_TEM, apiUrl, param, resMsg, "无");
        LOGGER.info(apiLog);
        return resMsg;
    }
}
