package com.pivot.aham.common.core.monitor;

import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
@Slf4j
public class PrometheusMonitorAspect {

    @Resource
    private  Counter requestTotal;
    @Resource
    private Counter requestError ;
    @Resource
    private Summary responseSummary ;

    @Pointcut("@annotation(com.pivot.aham.common.core.monitor.PrometheusMethodMonitor)")
    public void point() {
    }

    @Around(value = "point() && @annotation(annotation)")
    public Object MetricsCollector(ProceedingJoinPoint joinPoint, PrometheusMethodMonitor annotation) throws Throwable {
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        PrometheusMethodMonitor prometheusMetrics = methodSignature.getMethod().getAnnotation(PrometheusMethodMonitor.class);
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        String activeProfile = ApplicationContextHolder.getActiveProfile();
        if(annotation != null) {
            String name;
            if(StringUtils.isNotEmpty(annotation.name())) {
                name = annotation.name();
            } else {
                name = className+"."+methodName;
            }
            requestTotal.labels(name,activeProfile).inc();
            Summary.Timer requestTimer = responseSummary.labels(name,activeProfile).startTimer();
            Object object;
            try {
                object = joinPoint.proceed();
            } catch (Exception e) {
                requestError.labels(e.getClass().getName(),name,activeProfile).inc();
                throw e;
            } finally {
                requestTimer.observeDuration();
            }
            return object;
        } else {
            return joinPoint.proceed();
        }
    }

}