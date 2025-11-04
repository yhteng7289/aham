package com.pivot.aham.common.core.filter;

import com.pivot.aham.common.core.util.WebUtil;
import com.pivot.aham.common.model.SessionUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * session中的用户信息转入到request，登录成功后，接入到shiro的过滤链中
 *
 * @author addison
 * @since 2018年11月15日
 */
public class SessionFilter implements Filter {
    private Logger logger = LogManager.getLogger();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("init SessionFilter.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        SessionUser sessionUser = (SessionUser)SecurityUtils.getSubject().getPrincipal();
        if (sessionUser != null) {
            WebUtil.saveCurrentUser((HttpServletRequest)request, sessionUser);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("destroy SessionFilter.");
    }
}
