package com.pivot.aham.common.core.filter;

import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.FileUtil;
import com.pivot.aham.common.core.util.WebUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 跨站请求白名单拦截
 *
 * @author addison
 * @since 2018年11月15日
 */
public class CsrfFilter implements Filter {
    private Logger logger = LogManager.getLogger();

    // 白名单
    private List<String> whiteUrls;

    private int whiteUrl_size = 0;

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("init CsrfFilter..");
        // 读取白名单文件
        String path = CsrfFilter.class.getResource("/").getFile();
        whiteUrls = FileUtil.readFile(path + "white/csrfWhite.txt");
        whiteUrl_size = null == whiteUrls ? 0 : whiteUrls.size();

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest)request;
            // 获取请求url地址
            String url = req.getRequestURL().toString();
            String referurl = req.getHeader("Referer");
            //验证白名单
            if (WebUtil.isWhiteRequest(referurl, whiteUrl_size, whiteUrls)) {
                chain.doFilter(request, response);
            } else {
                // 记录跨站请求日志
                logger.warn("跨站请求---->>>{} || {} || {} || {}", url, referurl, WebUtil.getHost(req),
                    DateUtils.getDateTime());
                WebUtil.write(response, 308, "错误的请求头信息");
                return;
            }
        } catch (Exception e) {
            logger.error("doFilter", e);
        }
    }

    @Override
    public void destroy() {
        logger.info("destroy CsrfFilter.");
    }
}
