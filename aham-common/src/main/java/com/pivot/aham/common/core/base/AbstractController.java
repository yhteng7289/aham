package com.pivot.aham.common.core.base;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.common.model.SessionUser;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.WebUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSON;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 控制器abstract类
 *
 * @author addison
 * @since 2018年11月15日
 */
public abstract class AbstractController {

    protected Logger logger = LogManager.getLogger();

    public static final Date SITE_START_TIMESTAMP = new Date();
    public static final String LIND_FEED = System.getProperty("line.separator", "\n");
    public static final String BUILD_VERSION = "201603024-11:00";

    public static final String CURRENT_LOGIN_USER = "current_login_user";
    public static final String CURRENT_LOGIN_PWD = "current_login_pwd";
    public static final String CURRENT_LOGIN_PHONE = "current_login_phone";
    public static final String CURRENT_LOGIN_USER_PORTFOLIOID = "current_user_portfolioId";
    public static final String CURRENT_LOGIN_USER_ACCOUNT = "current_user_account";

    @Resource
    private RedissonHelper redissonHelper;

    /**
     * 获取当前用户Id(shiro)
     */
    protected SessionUser getCurrUser() {
        if (SecurityUtils.getSubject() == null || SecurityUtils.getSubject().getPrincipal() == null) {
            return null;
        }
        return (SessionUser) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取当前用户Id
     *
     * @param request
     * @return
     */
    protected Long getCurrUser(HttpServletRequest request) {
        SessionUser user = WebUtil.getCurrentUser(request);
        if (user == null) {
            return null;
        } else {
            return user.getId();
        }
    }

    /**
     * 设置成功响应代码
     *
     * @return
     */
    protected ResponseEntity<ModelMap> setSuccessModelMap() {
        return setSuccessModelMap(new ModelMap(), null);
    }

    /**
     * 设置成功响应代码
     *
     * @param modelMap
     * @return
     */
    protected ResponseEntity<ModelMap> setSuccessModelMap(ModelMap modelMap) {
        return setSuccessModelMap(modelMap, null);
    }

    /**
     * 设置成功响应代码
     *
     * @param data
     * @return
     */
    protected ResponseEntity<ModelMap> setSuccessModelMap(Object data) {
        return setModelMap(new ModelMap(), MessageStandardCode.OK, data);
    }

    /**
     * 设置成功响应代码
     *
     * @param modelMap
     * @param data
     * @return
     */
    protected ResponseEntity<ModelMap> setSuccessModelMap(ModelMap modelMap, Object data) {
        return setModelMap(modelMap, MessageStandardCode.OK, data);
    }

    /**
     * 设置响应代码
     *
     * @param code
     * @return
     */
    protected ResponseEntity<ModelMap> setModelMap(MessageStandardCode code) {
        return setModelMap(new ModelMap(), code, null);
    }

    /**
     * 设置响应代码
     *
     * @param code
     * @param msg
     * @return
     */
    protected ResponseEntity<ModelMap> setModelMap(String code, String msg) {
        return setModelMap(new ModelMap(), code, msg, null);
    }

    /**
     * 设置响应代码
     *
     * @param modelMap
     * @param code
     * @return
     */
    protected ResponseEntity<ModelMap> setModelMap(ModelMap modelMap, MessageStandardCode code) {
        return setModelMap(modelMap, code, null);
    }

    /**
     * 设置响应代码
     *
     * @param code
     * @param data
     * @return
     */
    protected ResponseEntity<ModelMap> setModelMap(MessageStandardCode code, Object data) {
        return setModelMap(new ModelMap(), code, data);
    }

    /**
     * 设置响应代码
     *
     * @param code
     * @param msg
     * @param data
     * @return
     */
    protected ResponseEntity<ModelMap> setModelMap(String code, String msg, Object data) {
        return setModelMap(new ModelMap(), code, msg, data);
    }

    /**
     * 设置响应代码
     *
     * @param modelMap
     * @param code
     * @param data
     * @return
     */
    protected ResponseEntity<ModelMap> setModelMap(ModelMap modelMap, MessageStandardCode code, Object data) {
        return setModelMap(modelMap, code.value().toString(), code.msg(), data);
    }

    /**
     * 设置响应代码
     *
     * @param modelMap
     * @param code
     * @param msg
     * @param data
     * @return ResponseEntity
     */
    protected ResponseEntity<ModelMap> setModelMap(ModelMap modelMap, String code, String msg, Object data) {
        if (!modelMap.isEmpty()) {
            Map<String, Object> map = InstanceUtil.newLinkedHashMap();
            map.putAll(modelMap);
            modelMap.clear();
            for (String key : map.keySet()) {
                if (!key.startsWith("org.springframework.validation.BindingResult") && !key.equals("void")) {
                    modelMap.put(key, map.get(key));
                }
            }
        }
        if (data != null) {
            if (data instanceof Page<?>) {
                Page<?> page = (Page<?>) data;
                modelMap.put("rows", page.getRecords());
                modelMap.put("current", page.getCurrent());
                modelMap.put("size", page.getSize());
                modelMap.put("pages", page.getPages());
                modelMap.put("total", page.getTotal());
            } else if (data instanceof List<?>) {
                modelMap.put("rows", data);
                modelMap.put("total", ((List<?>) data).size());
            } else {
                modelMap.put("data", data);
            }
        }
        modelMap.put("code", code);
        modelMap.put("msg", msg);
        modelMap.put("timestamp", System.currentTimeMillis());
        logger.info("返回参数===>{}", JSON.toJSONString(modelMap));
        return ResponseEntity.ok(modelMap);
    }

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    public static String getSiteStatusVersion(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long freeMemory = SystemInfo.getFreeMemery() / (1024 * 1024);
        long totalMemory = SystemInfo.getTotalMemery() / (1024 * 1024);
        String cpuRatio = String.format("%.3f%%", SystemInfo.getCpuRate() * 100.0);

        Double minutes = DateUtils.getBetween(DateUtils.now(), SITE_START_TIMESTAMP) / 60.0;
        String buildAt = String.format("%s (%.2f 分钟前 = %.2f 小时前 = %.2f 天前)", DateUtils.getPlusTime(SITE_START_TIMESTAMP), minutes.doubleValue(), minutes.doubleValue() / 60.0, minutes.doubleValue() / 60.0 / 24.0);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("Message: Hello JIMU-" + LIND_FEED
                + "Version: " + BUILD_VERSION + LIND_FEED
                + "Build-At: " + buildAt + LIND_FEED
                + "Server-At: " + DateUtils.getNowPlusTime() + LIND_FEED
                + "User-Agent: " + request.getHeader("User-Agent") + LIND_FEED
                + "Free-Memory: " + freeMemory + "/" + totalMemory + " M" + LIND_FEED
                + "CPU-Usage: " + cpuRatio);
        return null;
    }

    public void setLoginUser(HttpServletRequest request, String clientId, String virtualAcctNoSgd, String password, String phoneNumber, String portfolioId) {
        redissonHelper.set(CURRENT_LOGIN_USER + clientId, clientId, 3600);
        redissonHelper.set(CURRENT_LOGIN_USER_ACCOUNT + "_" + clientId, virtualAcctNoSgd, 3600);
        redissonHelper.set(DigestUtil.md5Hex(clientId), clientId, 3600);
        redissonHelper.set(CURRENT_LOGIN_PWD + "_" + clientId, password, 3600);
        redissonHelper.set(CURRENT_LOGIN_PHONE + "_" + clientId, phoneNumber, 3600);
        redissonHelper.set(CURRENT_LOGIN_USER_PORTFOLIOID + "_" + clientId, portfolioId);
        request.getSession().setAttribute(CURRENT_LOGIN_USER, clientId);
        request.getSession().setAttribute(CURRENT_LOGIN_PWD, password);
        request.getSession().setAttribute(CURRENT_LOGIN_PHONE, phoneNumber);
    }

    public void setCurrentWithdrawalOrderId(String clientId, String goalId, String newOrderId) {
        String withdrawKey = "WITHDRAW_" + clientId + "_" + goalId;
        String oldOrderId = redissonHelper.get(withdrawKey);
        if (oldOrderId != null) {
            redissonHelper.del(withdrawKey);
        }
        redissonHelper.set("WITHDRAW_" + clientId + "_" + goalId, newOrderId);
        redissonHelper.expire(withdrawKey, 20);
    }

    public String getCurrentWithdrawalOrderId(String clientId, String goalId) {
        String withdrawKey = "WITHDRAW_" + clientId + "_" + goalId;
        String orderId = redissonHelper.get(withdrawKey);
        redissonHelper.del(withdrawKey);
        return orderId;
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String getClientId() {
        HttpServletRequest request = getRequest();
        return (String) request.getSession().getAttribute(CURRENT_LOGIN_USER);
    }

    public String getClient() {
        HttpServletRequest request = getRequest();
        return (String) request.getSession().getAttribute(CURRENT_LOGIN_USER);
    }

    public String getPwd(String clientId) {
        return redissonHelper.get(CURRENT_LOGIN_PWD + "_" + clientId);
    }

    public String getPhoneNum(String clientId) {
        return redissonHelper.get(CURRENT_LOGIN_PHONE + "_" + clientId);
    }

    public boolean checkLogin(String clientId) {
        return checkLogin(clientId, "");
    }

    public boolean checkLogin(String clientId, String tokenStr) {
        String currentLoginUser = "";
        if (tokenStr.isEmpty()) {
            HttpServletRequest request = getRequest();
            tokenStr = request.getHeader("token");
            logger.info("requestUrl: {}, token:{}, clientId {} ", request.getRequestURL(), request.getHeader("token"), clientId);
        }
        if (clientId.isEmpty()) {
            return true;
        } else {
            currentLoginUser = redissonHelper.get(tokenStr);
        }
        logger.info("token:{} , currentLoginUser {}", clientId, currentLoginUser);
        if (currentLoginUser != null) {
            return DigestUtil.md5Hex(currentLoginUser).equals(tokenStr);
        } else {
            return false;
        }

    }
}
