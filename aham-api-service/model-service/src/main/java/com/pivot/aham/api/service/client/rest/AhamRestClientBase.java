package com.pivot.aham.api.service.client.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pivot.aham.api.service.client.RestClientBase;
import com.pivot.aham.api.service.client.rest.resp.AhamTokenResp;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.exception.HttpClientException;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.HttpResMsg;
import com.pivot.aham.common.core.util.HttpclientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
public class AhamRestClientBase extends RestClientBase {

    static HttpResMsg executeGet(String url, Map<String, String> params) {
        Header[] headers = createAuthHeader();
        try {
            log.info("====================Run executeGet===============================");
            log.info("url: {} , headers: {} , param: {}", url, headers, JSON.toJSONString(params));
            HttpResMsg resMsg = HttpclientUtils.get(url, params, headers);
            log.info("url: {} , headers: {} , param: {} , response: {}", url, headers, JSON.toJSONString(params), JSON.toJSONString(resMsg));
            log.info("====================Completed Run executeGet===============================");
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

    static HttpResMsg executePostTrade(String url, String prdCode, String transType, Integer unit, String orderId, Date transDate) {
    //static HttpResMsg executePost(String url, Map<String, String> requestBody) {
        //Header[] headers = createAuthHeader();
        try {
            JSONObject bodyObject = new JSONObject();
            bodyObject.put("pTransactionDate", DateUtils.formatDate(transDate, DateUtils.DATE_FORMAT7));
            bodyObject.put("pScheme", prdCode);
            bodyObject.put("pTransType", transType);
            bodyObject.put("pUnit", unit);
            bodyObject.put("pOrderID", orderId);
            bodyObject.put("pAgentID", "RZ00000");
            
            //bodyObject.put("pTransactionDate", "2021-07-29 17:30:00");
            //bodyObject.put("pScheme", "SOF");
            //bodyObject.put("pTransType", "RD");
            //bodyObject.put("pUnit", "100.00");
            //bodyObject.put("pOrderID", "1418499234776317953");
            //bodyObject.put("pAgentID", "RZ00000");

            JSONObject headerObject = new JSONObject();
            headerObject.put("SubmitModelPortfolioOrder", bodyObject);
            
            //Header[] headers = new Header[]{new BasicHeader("Authorization", "Bearer p8ba9ngurxa4ekqw7nfwyery")};
             Header[] headers = createAuthHeader();
            
            log.info("====================Run executePost===============================");
            log.info("url: {} , headers: {} , param: {}", url, headers, JSON.toJSONString(headerObject));
            HttpResMsg resMsg = HttpclientUtils.post(url, JSON.toJSONString(headerObject), HttpclientUtils.CHARSET_UTF8, headers);
            log.info("url: {} , headers: {},param: {} , response: {}", url, headers, JSON.toJSONString(headerObject), JSON.toJSONString(resMsg));
            log.info("====================Completed Run executePost===============================");
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
    
    static HttpResMsg executePost(String url, Map<String, Object> requestBody) {
        Header[] headers = createAuthHeader();
        try {
            log.info("====================Run executePost===============================");
            log.info("url: {} , headers: {} , param: {}", url, headers, JSON.toJSONString(requestBody));
            HttpResMsg resMsg = HttpclientUtils.post(url, JSON.toJSONString(requestBody), HttpclientUtils.CHARSET_UTF8, headers);
            log.info("url: {} , headers: {},param: {} , response: {}", url, headers, JSON.toJSONString(requestBody), JSON.toJSONString(resMsg));
            log.info("====================Completed Run executePost===============================");
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
//            PreTestInternet.requestGoogle();
            log.info("====================Run executePatch===============================");
            log.info("url: {} , headers: {} , param: {}", url, headers, JSON.toJSONString(requestBody));
            HttpResMsg resMsg = HttpclientUtils.patch(url, JSON.toJSONString(requestBody), HttpclientUtils.CHARSET_UTF8, headers);
            log.info("url: {} , param: {} , response: {}", url, JSON.toJSONString(requestBody), JSON.toJSONString(resMsg));
            log.info("====================Completed Run executePatch===============================");
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
//            PreTestInternet.requestGoogle();
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
            throw new BusinessException("Failed to obtain token");
        }
        return new Header[]{new BasicHeader("Authorization", "Bearer " + authVal), new BasicHeader("Ocp-Apim-Subscription-Key", "995245a4b4784ac097f71a7155d6cb56")};
    }

    static String getToken() {

        //temporary for testing

        if (StringUtils.isEmpty(getRedissonHelper().get(AhamRestConstant.AccessFundTokenKey))) {
            refreshToken();
        }
        return getRedissonHelper().get(AhamRestConstant.AccessFundTokenKey);
//        return "kvqyrxty6twaty54jred4u7p";
    }

    public static void refreshToken() {

        String authVal = "Basic " + Base64.getEncoder().encodeToString((AhamRestConstant.getTokenAppKey() + ":" + AhamRestConstant.getTokenAppSecret()).getBytes());
        Header[] headers = {new BasicHeader("Authorization", authVal)};
        log.info("AhamRestConstant.getTokenAppKey():{}", AhamRestConstant.getTokenAppKey());
        log.info("AhamRestConstant.getTokenAppSecret()).getBytes():{}", AhamRestConstant.getTokenAppSecret().getBytes());
        log.info("authVal:{}", authVal);

        Map<String, String> requestBody = Maps.newHashMap();
        requestBody.put("grant_type", AhamRestConstant.getTokenGrantType());

        HttpResMsg response = null;
        try {
            response = HttpclientUtils.post(AhamRestConstant.getFundTokenAuthUrl(), requestBody, HttpclientUtils.CHARSET_UTF8, headers);
            log.info("refreshToken url: {} , param: {} , response: {}", AhamRestConstant.getFundTokenAuthUrl(), JSON.toJSONString(requestBody), JSON.toJSONString(response));
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }

        if (response != null && response.isSuccess()) {
            AhamTokenResp resp = JSON.parseObject(response.getResponseStr(), AhamTokenResp.class);
            getRedissonHelper().set(AhamRestConstant.AccessFundTokenKey, resp.getAccess_token());
        } else {
            log.error("Failed to get token: {} param: {}, response: {}", AhamRestConstant.getFundTokenAuthUrl(), JSON.toJSONString(requestBody), JSON.toJSONString(response));
            throw new HttpClientException("Failed to get token");
        }
    }
}
