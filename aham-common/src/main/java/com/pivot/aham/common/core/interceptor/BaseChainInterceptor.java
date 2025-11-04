package com.pivot.aham.common.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 责任链模式拦截器基础类
 *
 * @author addison
 * @since 2018年11月15日
 */
public class BaseChainInterceptor extends HandlerInterceptorAdapter {
    /**
     * 链条数组
     */
    private BaseChainInterceptor[] nextInterceptor;

    public void setNextInterceptor(BaseChainInterceptor... nextInterceptor) {
        this.nextInterceptor = nextInterceptor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        if (nextInterceptor == null) {
            return true;
        }
        for (int i = 0; i < nextInterceptor.length; i++) {
            if (!nextInterceptor[i].preHandle(request, response, handler)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (nextInterceptor != null) {
            for (int i = 0; i < nextInterceptor.length; i++) {
                nextInterceptor[i].postHandle(request, response, handler, modelAndView);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        if (nextInterceptor != null) {
            for (int i = 0; i < nextInterceptor.length; i++) {
                nextInterceptor[i].afterCompletion(request, response, handler, ex);
            }
        }
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        if (nextInterceptor != null) {
            for (int i = 0; i < nextInterceptor.length; i++) {
                nextInterceptor[i].afterConcurrentHandlingStarted(request, response, handler);
            }
        }
    }
}
