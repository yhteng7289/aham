package com.pivot.aham.common.core.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 多次读取requestbody
 *
 * @author addison
 * @since 2018年11月15日
 */
public class RequestWrapperFilter implements Filter {
    private Logger logger = LogManager.getLogger();



    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("init RequestWrapperFilter..");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        HttpServletRequest req = (HttpServletRequest)request;
        String url = req.getServletPath();
        if(request instanceof HttpServletRequest && !url.contains("actuator") && !url.contains("upload")) {
                requestWrapper = new RequestWrapper((HttpServletRequest) request);
        }
        if(null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy() {
        logger.info("destroy RequestWrapperFilter.");
    }
}
