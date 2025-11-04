package com.pivot.aham.common.core.monitor;

import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author addison
 */
@Aspect
@Component
@Slf4j
public class PrometheusRequestMappingAspect {

    @Resource
    private Counter apiRequestTotal;
    @Resource
    private Counter apiRequestError;
    @Resource
    private Summary apiResponseSummary;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.PostMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.GetMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.PutMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void point() {
    }

    @Around(value = "point()")
    public Object MetricsCollector(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String uri = request.getRequestURI();

            String activeProfile = ApplicationContextHolder.getActiveProfile();
            apiRequestTotal.labels(uri, activeProfile).inc();
            Summary.Timer requestTimer = apiResponseSummary.labels(uri, activeProfile).startTimer();
            Object object;
            try {
                object = joinPoint.proceed();
            } catch (Exception e) {
                apiRequestError.labels(e.getClass().getName(), uri, activeProfile).inc();
//            log.error("uri处理异常",e);
                throw e;
            } finally {
                requestTimer.observeDuration();
            }
            return object;
        }
        return null;
    }

}
