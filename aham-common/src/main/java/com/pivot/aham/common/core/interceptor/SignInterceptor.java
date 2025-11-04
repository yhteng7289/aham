package com.pivot.aham.common.core.interceptor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.MessageStandardCode;
import com.pivot.aham.common.core.util.DataUtil;
import com.pivot.aham.common.core.util.FileUtil;
import com.pivot.aham.common.core.util.WebUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 请求参数签名验证
 *
 * @author addison
 * @since 2018年11月15日
 */
public class SignInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LogManager.getLogger();
    /**
     * 白名单
     */
    private List<String> whiteUrls;
    private int whiteUrl_size = 1;

    public SignInterceptor() {
        // 读取文件
        String path = SignInterceptor.class.getResource("/").getFile();
        whiteUrls = FileUtil.readFile(path + "white/signWhite.txt");
        whiteUrl_size = null == whiteUrls ? 0 : whiteUrls.size();
//        //开发环境放开
//        if (ApplicationContextHolder.getActiveProfile().equals("qa") || ApplicationContextHolder.getActiveProfile().equals("prod")) {
        whiteUrl_size = 0;
//        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //指定接口不需要签名
        String url = request.getRequestURL().toString();
        String refer = request.getHeader("Referer");
        if (url != null) {
            logger.info("忽略签名");
            return true;
        }
        if (refer != null && refer.contains(Constants.SWAGGER_URL_PREFIX) || WebUtil.isWhiteRequest(url, whiteUrl_size, whiteUrls)) {
            logger.info("忽略签名");
            return true;
        }
        //获取签名
//        String sign = request.getHeader("sign");
//        if (DataUtil.isEmpty(sign)) {
//            return WebUtil.write(response, MessageStandardCode.NOT_ACCEPTABLE.value(), "请求参数未签名");
//        }
//        String timestamp = request.getHeader("timestamp");
//        if (DataUtil.isEmpty(timestamp)) {
//            return WebUtil.write(response, MessageStandardCode.NOT_ACCEPTABLE.value(), "请求非法");
//        }
//        logger.debug("Url {} Timestamp {} Sign {}", url,timestamp, sign);
//        if (Math.abs(System.currentTimeMillis() - Long.valueOf(timestamp)) > 1000 * 60 * 5) {
//            return WebUtil.write(response, MessageStandardCode.FORBIDDEN.value(), "请求已过期");
//        }
        // 获取参数
        Map<String, Object> paramBody = WebUtil.getParameter(request);
        String sign = (String) paramBody.get("sign");
        if (DataUtil.isEmpty(sign)) {
            return WebUtil.write(response, MessageStandardCode.NOT_ACCEPTABLE.value(), "请求参数未签名");
        }
//        String timestamp = (String) paramBody.get("timestamp");
//        if (DataUtil.isEmpty(timestamp)) {
//            return WebUtil.write(response, MessageStandardCode.NOT_ACCEPTABLE.value(), "请求非法");
//        }
//        if (Math.abs(System.currentTimeMillis() - Long.valueOf(timestamp)) > 1000 * 60 * 5) {
//            return WebUtil.write(response, MessageStandardCode.FORBIDDEN.value(), "请求已过期");
//        }
        logger.debug("Url {}  {} Sign {}", url, sign);
        String[] keys = paramBody.keySet().toArray(new String[]{});
//        Arrays.sort(keys);
        StringBuilder sb = new StringBuilder();
        sb.append("key=").append("ef576f0f7958b230679234e2e2a78b14");
        for (String key : keys) {
            if (!key.equals("sign") && !key.equals("timestamp")) {
                sb.append("&").append(key).append("=").append(paramBody.get(key));
            }
        }
        //md5签名
        String encrypt = DigestUtil.md5Hex(sb.toString());
        if (!encrypt.toLowerCase().equals(sign.toLowerCase())) {
            return WebUtil.write(response, MessageStandardCode.FORBIDDEN.value(), "签名错误");
        }
        logger.info("签名成功");
        return true;
    }


}
