package com.pivot.aham.common.core.support.context;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.util.ExceptionUtil;
import com.pivot.aham.common.core.util.WebUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSON;

import com.pivot.aham.common.core.util.InstanceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.annotation.AfterReturning;
import org.slf4j.LoggerFactory;

/**
 * 拦截controller的入参
 *
 * todo：放入requestContext
 *
 * @author addison
 * @since 2018年11月18日
 */
@Aspect
@Component
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RequestBodyAspect {

    private static final org.slf4j.Logger APP_LOGGER = LoggerFactory.getLogger("com.pivot.aham.api.web.app.controller");
    private static final org.slf4j.Logger WEB_LOGGER = LoggerFactory.getLogger("com.pivot.aham.api.web.web.controller");

    private static final Map<Class<?>, Method[]> METHOD_MAP = InstanceUtil.newHashMap();

    @Pointcut("execution(* *..*.web..*Controller.*(..))")
    public void requestBody() {
    }

    /**
     * 前置通知,使用在方法aspect()上注册的切入点
     *
     * @param pjp
     */
    @Before("requestBody()")
    public void before(JoinPoint pjp) {
        try {
            String methodName = pjp.getSignature().getName();
            Class<?> cls = pjp.getTarget().getClass();
            Method[] methods = getMethods(cls);
            L:
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    Parameter[] ps = method.getParameters();
                    for (int i = 0; i < ps.length; i++) {
                        Parameter parameter = ps[i];
                        Object value = pjp.getArgs()[i];
                        RequestBody rb = parameter.getAnnotation(RequestBody.class);
                        if (rb != null) {
                            String body = JSON.toJSONString(value);
                            String className = cls.getName();
                            // com.pivot.aham.api.web.app.controller -- api
                            // com.pivot.aham.api.web.web.controller -- web
                            if (className.contains("com.pivot.aham.api.web.app.controller")) {
                                APP_LOGGER.info("APP Request ClassName Method =>" + cls.getName() + "." + methodName);
                                APP_LOGGER.info("APP Request Body ===>{}", body);
                                log.info("请求方法=>" + cls.getName() + "." + methodName);
                                log.info("请求参数===>{}", body);
                            } else if (className.contains("com.pivot.aham.api.web.web.controller")) {
                                WEB_LOGGER.info("WEB Request ClassName Method=>" + cls.getName() + "." + methodName);
                                WEB_LOGGER.info("WEB Request Body ===>{}", body);
                                log.info("请求方法=>" + cls.getName() + "." + methodName);
                                log.info("请求参数===>{}", body);
                            } else {
                                log.info("请求方法=>" + cls.getName() + "." + methodName);
                                log.info("请求参数===>{}", body);
                            }
                            if (WebUtil.REQUEST.get() != null) {
                                WebUtil.REQUEST.get().setAttribute(Constants.REQUEST_BODY, body);
                            }
                            break L;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTraceAsString(e));
        }
    }

    public void after(JoinPoint pjp, Object result) {

    }

    @AfterReturning(returning = "ret", pointcut = "requestBody()")
    public void doAfterReturning(JoinPoint pjp, Object ret) throws Throwable {
        try {
            String methodName = pjp.getSignature().getName();
            Class<?> cls = pjp.getTarget().getClass();
            Method[] methods = getMethods(cls);
            L:
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    Parameter[] ps = method.getParameters();
                    for (int i = 0; i < ps.length; i++) {
                        Parameter parameter = ps[i];
                        Object value = pjp.getArgs()[i];
                        RequestBody rb = parameter.getAnnotation(RequestBody.class);
                        if (rb != null) {
                            String body = JSON.toJSONString(value);
                            String className = cls.getName();
                            // com.pivot.aham.api.web.app.controller -- api
                            // com.pivot.aham.api.web.web.controller -- web
                            log.info("className {} ", className);
                            if (className.contains("com.pivot.aham.api.web.app.controller")) {
                                APP_LOGGER.info("APP Request ClassName Method =>" + cls.getName() + "." + methodName);
                                APP_LOGGER.info("APP Request Body ===>{}", body);
                                APP_LOGGER.info("APP RESPONSE : " + ret);
                                log.info("请求方法=>" + cls.getName() + "." + methodName);
                                log.info("请求参数===>{}", body);
                            } else if (className.contains("com.pivot.aham.api.web.web.controller")) {
                                WEB_LOGGER.info("WEB Request ClassName Method=>" + cls.getName() + "." + methodName);
                                WEB_LOGGER.info("WEB Request Body ===>{}", body);
                                WEB_LOGGER.info("WEB RESPONSE : " + ret);
                                log.info("请求方法=>" + cls.getName() + "." + methodName);
                                log.info("请求参数===>{}", body);
                            } else {
                                log.info("请求方法=>" + cls.getName() + "." + methodName);
                                log.info("请求参数===>{}", body);
                            }
                            if (WebUtil.REQUEST.get() != null) {
                                WebUtil.REQUEST.get().setAttribute(Constants.REQUEST_BODY, body);
                            }
                            break L;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTraceAsString(e));
        }
        // 处理完请求，返回内容

    }

    private Method[] getMethods(Class<?> cls) {
        if (METHOD_MAP.containsKey(cls)) {
            return METHOD_MAP.get(cls);
        }
        Method[] methods = cls.getDeclaredMethods();
        METHOD_MAP.put(cls, methods);
        return methods;
    }

    private String getValue(Object result) {
        String returnValue = null;
        if (null != result) {
            if (result.toString().endsWith("@" + Integer.toHexString(result.hashCode()))) {
                returnValue = ReflectionToStringBuilder.toString(result);
            } else {
                returnValue = result.toString();
            }
        }
        return returnValue;
    }

}
