package com.pivot.aham.common.core.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.MessageStandardCode;
import com.pivot.aham.common.core.util.CacheUtil;
import com.pivot.aham.common.core.util.FileUtil;
import com.pivot.aham.common.core.util.WebUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 防止频繁请求
 *
 * @author addison
 * @since 2018年11月15日
 */
@Slf4j
public class MaliciousRequestInterceptor extends BaseChainInterceptor {
    /**
     * 拦截所有请求,否则拦截相同请求
     */
    private boolean allRequest = false;
    /**
     * 包含参数
     */
    private boolean containsParamter = true;
    /**
     * 允许的最小请求间隔，单位秒
     */
    private int minRequestIntervalTime = 5;
    /**
     * 允许的最大恶意请求次数
     */
    private int maxMaliciousTimes = 10;

    /**
     * 白名单
     */
    private List<String> whiteUrls;
    private int whiteUrl_size = 0;

    public MaliciousRequestInterceptor() {
        // 读取文件
        String path = MaliciousRequestInterceptor.class.getResource("/").getFile();
        whiteUrls = FileUtil.readFile(path + "white/mrqWhite.txt");
        whiteUrl_size = null == whiteUrls ? 0 : whiteUrls.size();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //获取请求path
        String url = request.getServletPath();

        if (url.endsWith("/unauthorized") || url.endsWith("/forbidden")
                || WebUtil.isWhiteRequest(url, whiteUrl_size, whiteUrls)) {
            return super.preHandle(request, response, handler);
        }
        if (containsParamter) {
            url += JSON.toJSONString(WebUtil.getParameterMap(request));
        }
        Object userId = WebUtil.getCurrentUser(request);
        String user = userId != null ? userId.toString() : WebUtil.getHost(request) + request.getHeader("USER-AGENT");
        String preRequest = (String) CacheUtil.getCache().getFire(Constants.PREREQUEST + user);
        Long preRequestTime = (Long)CacheUtil.getCache().getFire(Constants.PREREQUEST_TIME + user);
        int seconds = minRequestIntervalTime;
        // 过滤频繁操作
        if (preRequestTime != null && preRequest != null) {
            Boolean isPreOrAllAndExpire = (url.equals(preRequest) || allRequest)
            && System.currentTimeMillis() - preRequestTime < minRequestIntervalTime;
            if (isPreOrAllAndExpire) {
                Integer maliciousRequestTimes = (Integer)CacheUtil.getCache()
                        .getFire(Constants.MALICIOUS_REQUEST_TIMES + user);
                if (maliciousRequestTimes == null) {
                    maliciousRequestTimes = 1;
                } else {
                    maliciousRequestTimes++;
                }
                CacheUtil.getCache().set(Constants.MALICIOUS_REQUEST_TIMES + user, maliciousRequestTimes, seconds);
                if (maliciousRequestTimes > maxMaliciousTimes) {
                    CacheUtil.getCache().set(Constants.MALICIOUS_REQUEST_TIMES + user, 0, seconds);
                    log.warn("拦截恶意请求 : {}", url);
                    return WebUtil.write(response, MessageStandardCode.MULTI_STATUS.value(),
                        MessageStandardCode.MULTI_STATUS.msg());
                }
            } else {
                CacheUtil.getCache().set(Constants.MALICIOUS_REQUEST_TIMES + user, 0, seconds);
            }
        }
        CacheUtil.getCache().set(Constants.PREREQUEST + user, url, seconds);
        CacheUtil.getCache().set(Constants.PREREQUEST_TIME + user, System.currentTimeMillis(), seconds);
        return super.preHandle(request, response, handler);
    }

    public MaliciousRequestInterceptor setAllRequest(boolean allRequest) {
        this.allRequest = allRequest;
        return this;
    }

    public MaliciousRequestInterceptor setContainsParamter(boolean containsParamter) {
        this.containsParamter = containsParamter;
        return this;
    }

    public MaliciousRequestInterceptor setMinRequestIntervalTime(int minRequestIntervalTime) {
        this.minRequestIntervalTime = minRequestIntervalTime;
        return this;
    }

    public MaliciousRequestInterceptor setMaxMaliciousTimes(int maxMaliciousTimes) {
        this.maxMaliciousTimes = maxMaliciousTimes;
        return this;
    }
}
