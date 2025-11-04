package com.pivot.aham.common.core.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pivot.aham.common.core.filter.ResponseWrapper;
import com.pivot.aham.common.core.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.servlet.ShiroHttpServletResponse;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;

import com.alibaba.fastjson.JSON;

import io.swagger.annotations.ApiOperation;
import com.pivot.aham.common.model.SessionUser;
import com.pivot.aham.common.model.SysEvent;
import org.springframework.web.servlet.ModelAndView;

/**
 * 日志拦截器
 *
 * @author addison
 * @version 2018年11月14日 下午6:18:46
 */
@Slf4j
public class EventLogInterceptor extends BaseChainInterceptor {

    private final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("ThreadLocalStartTime");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 获取请求url地址
        String url = request.getServletPath();
        if (url.contains("actuator") || url.contains("upload")) {
            return super.preHandle(request, response, handler);
        }

        String path = request.getServletPath();
        // 开始时间（该数据只有当前请求的线程可见）
        startTimeThreadLocal.set(System.currentTimeMillis());
        Map<String, Object> params = WebUtil.getParameterMap(request);
        Map<String, Object> paramBody = WebUtil.getParameter(request);
        params.putAll(paramBody);
        log.info("接口URI {} 请求参数===>{}", path, JSON.toJSONString(params));
        WebUtil.REQUEST.set(request);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, HttpServletResponse response, Object handler,
            final Exception ex) throws Exception {

        // 获取请求url地址
        String url = request.getServletPath();
        if (url.contains("actuator") || url.contains("upload") || url.contains("notification")) {
            return;
        }

        final Long startTime = startTimeThreadLocal.get();

        final Long endTime = System.currentTimeMillis();
        String path = request.getServletPath();
        final SysEvent record = new SysEvent();
        // 保存日志
        if (handler instanceof HandlerMethod) {
            try {
                String userAgent = request.getHeader("USER-AGENT");
                String clientIp = WebUtil.getHost(request);
                SessionUser session = WebUtil.getCurrentUser(request);
                if (!path.contains("/error") && !path.contains("/read/")
                        && !path.contains("/query") && !path.contains("/detail")
                        && !path.contains("/unauthorized") && !path.contains("/forbidden")) {

                    record.setMethod(request.getMethod());
                    record.setRequestUri(path);
                    record.setClientHost(clientIp);
                    record.setUserAgent(userAgent);
                    if (path.contains("/upload")) {
                        record.setParameters("");
                    } else {
                        record.setParameters(JSON.toJSONString(WebUtil.getParameter(request)));
                    }
                    record.setStatus(response.getStatus());
                    if (session != null) {
                        record.setUserName(session.getUserName());
                        record.setUserPhone(session.getUserPhone());
                    }
                    final String msg = (String) request.getAttribute("msg");
                    try {
                        HandlerMethod handlerMethod = (HandlerMethod) handler;
                        ApiOperation apiOperation = handlerMethod.getMethod().getAnnotation(ApiOperation.class);
                        if (apiOperation != null) {
                            record.setTitle(apiOperation.value());
                        }
                    } catch (Exception e) {
                        log.error("", e);
                    }
                    if (response instanceof ResponseWrapper) {
                        ResponseWrapper responseWrapper = (ResponseWrapper) response;
                        record.setResponseStr(responseWrapper.getResponseData("UTF-8"));
                    }
                    if (response instanceof ShiroHttpServletResponse) {
                        ShiroHttpServletResponse shiroHttpServletResponse = (ShiroHttpServletResponse) response;
                        if (shiroHttpServletResponse.getResponse() instanceof ResponseWrapper) {
                            ResponseWrapper responseWrapper = (ResponseWrapper) shiroHttpServletResponse.getResponse();
                            record.setResponseStr(responseWrapper.getResponseData("UTF-8"));
                        }
                    }
                } else if (path.contains("/unauthorized")) {
                    log.info("用户 [{}] 未登录", clientIp + "@" + userAgent);
                } else if (path.contains("/forbidden")) {
                    log.info("用户 [{}] 没有权限", JSON.toJSONString(session) + "@" + clientIp + "@" + userAgent);
                } else {
                    log.info(JSON.toJSONString(session) + "@" + path + "@" + clientIp + userAgent);
                }
            } catch (Throwable e) {
                log.error("日志处理发生异常", e);
            }
            // 内存信息
            String message = "接口URI: {}; 开始时间: {}; 结束时间: {}; 使用时间: {}s; Record:{} ";
            log.info(message, path, startTime, endTime, String.valueOf((endTime - startTime) / 1000.00), JSON.toJSON(record));
        }
//        startTimeThreadLocal.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
