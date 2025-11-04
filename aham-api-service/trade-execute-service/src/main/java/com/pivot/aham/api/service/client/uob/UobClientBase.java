package com.pivot.aham.api.service.client.uob;

import com.pivot.aham.api.service.client.ClientBase;
import com.pivot.aham.common.core.util.HttpResMsg;
import com.pivot.aham.common.core.util.PropertiesUtil;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UobClientBase extends ClientBase {

    static boolean isProd() {
        return PropertiesUtil.isProd();
    }

    static HttpResMsg executePost(String url, Map<String, Object> requestBody) {
//        Header[] headers = createAuthHeader();
//        final PrivateKey privateKey = SecurityKeyUtils.parseRSAPrivateKey(privateKeyCert);
//        try {
//            
//            log.info("url: {} , headers: {} , param: {}", url, headers, JSON.toJSONString(requestBody));
//            HttpResMsg resMsg = HttpclientUtils.post(url, JSON.toJSONString(requestBody), HttpclientUtils.CHARSET_UTF8, headers);
//            log.info("url: {} , headers: {},param: {} , response: {}", url, headers, JSON.toJSONString(requestBody), JSON.toJSONString(resMsg));
//            if (resMsg != null && resMsg.getStatusCode() == 401) {
//                
//            } else {
//                return resMsg;
//            }
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
        return null;
    }

//    private static Header[] createAuthHeader() {
//        if (StringUtils.isEmpty(authVal)) {
//            throw new BusinessException("获取token失败");
//        }
//        return new Header[]{new BasicHeader("Authorization", "Bearer " + authVal)};
//    }
}
