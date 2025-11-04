package com.pivot.aham.common.core.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.servlet.*;
import java.io.IOException;
import java.util.Locale;

/**
 * 国际化过滤器,默认中文
 *
 * @author addison
 * @since 2018年11月15日
 */
public class LocaleFilter implements Filter {
    private Logger logger = LogManager.getLogger();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("init LocaleFilter.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 设置客户端语言
        Locale locale = request.getLocale();
        if (locale == null) {
            String language = request.getParameter("locale");
            if (StringUtils.isNotBlank(language)) {
                locale = new Locale(language);
            } else {
                locale = Locale.SIMPLIFIED_CHINESE;
            }
        }
        LocaleContextHolder.setLocale(locale);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("destroy LocaleFilter.");
    }
}
