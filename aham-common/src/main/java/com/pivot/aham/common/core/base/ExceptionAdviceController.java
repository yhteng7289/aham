package com.pivot.aham.common.core.base;

import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.fastjson.JSON;
import com.pivot.aham.common.core.exception.BaseException;
import com.pivot.aham.common.core.exception.IllegalParameterException;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.core.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * controller通知
 *
 * @author addison
 * @since 2018年11月15日
 */
@ControllerAdvice
@Slf4j
public class ExceptionAdviceController {

    private static final String EMAIL_TITLE_TEM = "【{0}】环境 请求路径:{1} 发送未知异常";
    /**
     * 邮件线程池 采用DiscardOldestPolicy策略，丢掉时间最长的那个任务
     */
    private static ExecutorService executorService = new ThreadPoolExecutor(2, 20, 5, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.DiscardOldestPolicy());

    /**
     * 全局异常处理
     *
     * @param request
     * @param response
     * @param ex
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Message> exceptionHandler(HttpServletRequest request, HttpServletResponse response,
            Throwable ex) {
        log.error("程序异常:", ex);
        if (ex.getCause() instanceof FlowException) {
            FlowException flowException = (FlowException) ex.getCause();
            log.error("资源:{},超出流控限制,流控规则:{}", flowException.getRule().getResource(), JSON.toJSONString(flowException.getRule()));
        }
        Message message = new Message();
        String path = request.getServletPath();
        if (ex instanceof BaseException) {
            ((BaseException) ex).handler(message);
        } else if (ex instanceof IllegalArgumentException) {
            new IllegalParameterException(ex.getMessage()).handler(message);
        } else if ("org.apache.shiro.authz.UnauthorizedException".equals(ex.getClass().getName())) {
            message.setResultCode(MessageStandardCode.FORBIDDEN.value());
            message.setErrMsg(MessageStandardCode.FORBIDDEN.msg());
        } else if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exs = (MethodArgumentNotValidException) ex;

            message.setResultCode(MessageStandardCode.BAD_REQUEST.value());
            List<ObjectError> objectErrorList = exs.getBindingResult().getAllErrors();
            StringBuilder stringBuilder = new StringBuilder();
            for (ObjectError objectError : objectErrorList) {
                if (objectError instanceof FieldError) {
                    FieldError fieldError = (FieldError) objectError;
                    stringBuilder.append("[").append(fieldError.getField())
                            .append(fieldError.getDefaultMessage()).append("] ");
                } else {
                    stringBuilder.append("[").append(objectError).append("] ");
                }
            }
            message.setErrMsg(stringBuilder.toString());
        } else {

            message.setResultCode(MessageStandardCode.INTERNAL_SERVER_ERROR.value());
            String msg = StringUtils.defaultIfBlank(ex.getMessage(), MessageStandardCode.INTERNAL_SERVER_ERROR.msg());
            message.setErrMsg(msg.length() > 100 ? "System is busying, Please try again" : msg);

            //未知异常，预留邮件告警
            String contactEmail = PropertiesUtil.getString("unKonwException_contactEmail");
            String activeProfile = ApplicationContextHolder.getActiveProfile();
            String topic = MessageFormat.format(EMAIL_TITLE_TEM, activeProfile, path);

            Email email = new Email()
                    .setTemplateName("ExceptionEmail")
                    .setTemplateVariables(InstanceUtil.newHashMap("exMsg", ExceptionUtils.getFullStackTrace(ex)))
                    .setSendTo(contactEmail)
                    .setTopic(topic);
            executorService.submit(() -> EmailUtil.sendEmail(email));
        }
        Map<String, Object> params = WebUtil.getParameterMap(request);
        Map<String, Object> paramBody = WebUtil.getParameter(request);
        params.putAll(paramBody);
        log.info("请求路径 {} 参数===>{},异常返回===>{}", path, JSON.toJSONString(params), JSON.toJSON(message));

        return ResponseEntity.ok(message);
//       ResponseEntity.BodyBuilder bodyBuilder =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
//        return bodyBuilder.body(Message.error("error"));
    }

    public static void main(String[] args) {
        Email email = new Email()
                .setTemplateName("ExceptionEmail")
                .setTemplateVariables(InstanceUtil.newHashMap("exMsg", "111111112222"))
                .setSendTo("wooitatt.khor@ezyit.asia")
                .setTopic("testtest");
        EmailUtil.sendEmail(email);

        Email email1 = new Email()
                .setTemplateName("ExceptionEmail")
                .setTemplateVariables(InstanceUtil.newHashMap("exMsg", "111111112222"))
                .setSendTo("wooitatt.khor@ezyit.asia")
                .setTopic("testtest");
        EmailUtil.sendEmail(email1);
    }
}
