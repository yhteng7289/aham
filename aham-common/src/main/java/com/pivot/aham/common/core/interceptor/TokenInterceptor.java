package com.pivot.aham.common.core.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.crypto.digest.DigestUtil;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.MessageStandardCode;
import com.pivot.aham.common.core.util.CacheUtil;
import com.pivot.aham.common.core.util.DataUtil;
import com.pivot.aham.common.core.util.FileUtil;
import com.pivot.aham.common.core.util.WebUtil;
import com.pivot.aham.common.model.SessionUser;
import com.pivot.aham.common.core.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * token+签名验证
 * @author addison
 * @since 2018年11月12日 下午10:40:38
 */
@Slf4j
public class TokenInterceptor extends BaseChainInterceptor {

    private SignInterceptor signInterceptor;
    // 白名单
    private List<String> whiteUrls;
    private int whiteUrl_size = 0;

    public TokenInterceptor() {
        signInterceptor = new SignInterceptor();
        // 读取文件
        String path = TokenInterceptor.class.getResource("/").getFile();
        whiteUrls = FileUtil.readFile(path + "white/tokenWhite.txt");
        whiteUrl_size = null == whiteUrls ? 0 : whiteUrls.size();
        //开发环境放开
//        if(ApplicationContextHolder.getActiveProfile().equals("dev") || ApplicationContextHolder.getActiveProfile().equals("prod")){
            whiteUrl_size = 0;
//        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        SessionUser session = null;
        // 获取token
        String token = request.getHeader(Constants.TOKEN_NAME);
        if (DataUtil.isNotEmpty(token)) {
            String cacheKey = Constants.TOKEN_KEY + DigestUtil.md5Hex(token);
            session = (SessionUser) CacheUtil.getCache().get(cacheKey);
            if (DataUtil.isNotEmpty(session)) {
                request.setAttribute(Constants.CURRENT_USER, session);
                CacheUtil.getCache().expire(cacheKey, PropertiesUtil.getInt("APP-TOKEN-EXPIRE", 60 * 60 * 24 * 7));
            }
        }
        // 请求秘钥的接口不需要签名
        String url = request.getRequestURL().toString();
        String refer = request.getHeader("Referer");
        if (refer != null && refer.contains(Constants.SWAGGER_URL_PREFIX) || WebUtil.isWhiteRequest(url, whiteUrl_size, whiteUrls)) {
            log.info("跳过token验证");
            if (signInterceptor.preHandle(request, response, handler)) {
                return super.preHandle(request, response, handler);
            }
            return false;
        }
        if (DataUtil.isEmpty(token)) {
            return WebUtil.write(response, MessageStandardCode.UNAUTHORIZED.value(), "请登录");
        }
        log.debug("APP-TOKEN {}", token);
        // 判断token是否过期
        if (DataUtil.isEmpty(session)) {
            return WebUtil.write(response, MessageStandardCode.UNAUTHORIZED.value(), "会话已过期");
        } else {
            if (signInterceptor.preHandle(request, response, handler)) {
                log.info("token验证成功");
                return super.preHandle(request, response, handler);
            }
            return false;
        }
    }
}
