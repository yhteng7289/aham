package com.pivot.aham.common.core.filter;

import com.pivot.aham.common.core.util.WebUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 多次读取response
 *
 * @author addison
 * @since 2018年11月15日
 */
public class ResponseWrapperFilter implements Filter {

    private Logger logger = LogManager.getLogger();

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("init ResponseWrapperFilter..");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ServletResponse responseWrapper = null;
        HttpServletRequest req = (HttpServletRequest)request;
        // 获取请求url地址
        String url = req.getServletPath();

        if(response instanceof HttpServletResponse && !url.contains("actuator") && !url.contains("upload")) {
            responseWrapper = new ResponseWrapper((HttpServletResponse) response);
        }
        if(responseWrapper != null) {
            chain.doFilter(request, responseWrapper);
        } else {
            chain.doFilter(request, response);
        }
        WebUtil.REQUEST.remove();
    }

    @Override
    public void destroy() {
        logger.info("destroy ResponseWrapperFilter.");
    }
}
