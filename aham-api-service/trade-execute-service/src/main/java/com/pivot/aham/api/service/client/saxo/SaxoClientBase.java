package com.pivot.aham.api.service.client.saxo;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.Maps;
import com.pivot.aham.api.service.client.ClientBase;
import com.pivot.aham.api.service.client.saxo.resp.QueryTokenResp;
import com.pivot.aham.api.service.client.saxo.util.PreTestInternet;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.exception.HttpClientException;
import com.pivot.aham.common.core.support.keystore.KeyStoreUtil;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.HttpResMsg;
import com.pivot.aham.common.core.util.HttpclientUtils;
import com.pivot.aham.common.core.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

@Slf4j
class SaxoClientBase extends ClientBase {

    static boolean isProd() {
        return PropertiesUtil.isProd();
    }

    static HttpResMsg executePost(String url, Map<String, Object> requestBody) {
        Header[] headers = createAuthHeader();
        try {
            // PreTestInternet.requestGoogle(); //Edit By WooiTatt
            refreshToken();
            log.info("====================Run executePost==============================="); //Added By Wooi Tatt
            log.info("url: {} , headers: {} , param: {}", url, headers, JSON.toJSONString(requestBody));
            HttpResMsg resMsg = HttpclientUtils.post(url, JSON.toJSONString(requestBody), HttpclientUtils.CHARSET_UTF8, headers);
            log.info("url: {} , headers: {},param: {} , response: {}", url, headers, JSON.toJSONString(requestBody), JSON.toJSONString(resMsg));
            log.info("====================Completed Run executePost===============================");//Added By WooiTatt
            if (resMsg != null && resMsg.getStatusCode() == 401) {
                refreshToken();
            } else {
                return resMsg;
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        return null;
    }

    static HttpResMsg executeGet(String url, Map<String, String> params) {
        Header[] headers = createAuthHeader();
        try {
            PreTestInternet.requestGoogle();
            refreshToken();
            log.info("url: {} , headers: {} , param: {}", url, headers, JSON.toJSONString(params));
            HttpResMsg resMsg = HttpclientUtils.get(url, params, headers);
            log.info("url: {} , headers: {} , param: {} , response: {}", url, headers, JSON.toJSONString(params), JSON.toJSONString(resMsg));
            if (resMsg != null && resMsg.getStatusCode() == 401) {
                refreshToken();
            } else {
                return resMsg;
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        return null;
    }

    static HttpResMsg executePatch(String url, Map<String, Object> requestBody) {
        Header[] headers = createAuthHeader();

        try {
            PreTestInternet.requestGoogle();
            refreshToken();
            log.info("url: {} , headers: {} , param: {}", url, headers, JSON.toJSONString(requestBody));
            HttpResMsg resMsg = HttpclientUtils.patch(url, JSON.toJSONString(requestBody), HttpclientUtils.CHARSET_UTF8, headers);
            log.info("url: {} , param: {} , response: {}", url, JSON.toJSONString(requestBody), JSON.toJSONString(resMsg));
            if (resMsg != null && resMsg.getStatusCode() == 401) {
                refreshToken();
            } else {
                return resMsg;
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        return null;
    }

    static HttpResMsg executeDelete(String url, Map<String, String> params) {
        Header[] headers = createAuthHeader();
        try {
            PreTestInternet.requestGoogle();
            refreshToken();
            log.info("url: {} , headers: {} , param: {}", url, headers, JSON.toJSONString(params));
            HttpResMsg resMsg = HttpclientUtils.delete(url, params, headers);
            log.info("url: {} , param: {} , response: {}", url, JSON.toJSONString(params), JSON.toJSONString(resMsg));
            if (resMsg != null && resMsg.getStatusCode() == 401) {
                refreshToken();
            } else {
                return resMsg;
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        return null;
    }

    private static Header[] createAuthHeader() {
        String authVal = getToken();
        if (StringUtils.isEmpty(authVal)) {
            throw new BusinessException("获取token失败");
        }
        return new Header[]{new BasicHeader("Authorization", "Bearer " + authVal)};
    }

    static String getToken() {
        if (StringUtils.isEmpty(getRedissonHelper().get(SaxoConstants.AccessTokenKey))) {
            refreshToken();
        }
        return getRedissonHelper().get(SaxoConstants.AccessTokenKey);
    }

    public static void refreshToken() {
        String filePath = SaxoConstants.getTokenKeyStoreFilePath();
        String password = SaxoConstants.getTokenKeyStoreFilePassword();

        RSAPrivateKey privateKey = (RSAPrivateKey) KeyStoreUtil.getPrivateKey(filePath, password);
        RSAPublicKey publicKey = (RSAPublicKey) KeyStoreUtil.getPublicKey(filePath, password);
        Certificate certificate = KeyStoreUtil.getCertificate(filePath, password);
        String thumb = KeyStoreUtil.getThumbprint(certificate).toUpperCase();

        Map<String, Object> headerClaims = Maps.newHashMap();
        headerClaims.put("x5t", thumb);
        headerClaims.put("kid", thumb);

        String token = JWT.create()
                .withIssuer(SaxoConstants.getTokenAppKey())
                .withAudience(SaxoConstants.getTokenAuthUrl())
                .withHeader(headerClaims)
                .withClaim("spurl", SaxoConstants.getTokenAppUrl())
                .withClaim("sub", SaxoConstants.getUserId())
                .withClaim("iss", SaxoConstants.getTokenAppKey())
                .withClaim("aud", SaxoConstants.getTokenAuthUrl())
                .withExpiresAt(DateUtils.addHours(DateUtils.now(), 24))
                .sign(Algorithm.RSA256(publicKey, privateKey));

        String authVal = "Basic " + Base64.getEncoder().encodeToString((SaxoConstants.getTokenAppKey() + ":" + SaxoConstants.getTokenAppSecret()).getBytes());
        Header[] headers = {new BasicHeader("Authorization", authVal)};

        Map<String, String> requestBody = Maps.newHashMap();
        requestBody.put("assertion", token);
        requestBody.put("grant_type", SaxoConstants.getTokenGrantType());

        HttpResMsg response = null;
        try {
            PreTestInternet.requestGoogle();
            response = HttpclientUtils.post(SaxoConstants.getTokenAuthUrl(), requestBody, HttpclientUtils.CHARSET_UTF8, headers);
            log.info("refreshToken url: {} , param: {} , response: {}", SaxoConstants.getTokenAuthUrl(), JSON.toJSONString(requestBody), JSON.toJSONString(response));
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }

        if (response != null && response.isSuccess()) {
            QueryTokenResp resp = JSON.parseObject(response.getResponseStr(), QueryTokenResp.class);
            getRedissonHelper().set(SaxoConstants.AccessTokenKey, resp.getAccess_token());
            getRedissonHelper().set(SaxoConstants.RefreshTokenKey, resp.getRefresh_token());
        } else {
            log.error("刷新token失败: {} param: {}, response: {}", SaxoConstants.getTokenAuthUrl(), JSON.toJSONString(requestBody), JSON.toJSONString(response));
            throw new HttpClientException("刷新token失败");
        }
    }
}
