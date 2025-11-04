/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.client.saxo.util;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.common.core.util.ExceptionUtil;
import com.pivot.aham.common.core.util.HttpResMsg;
import com.pivot.aham.common.core.util.HttpclientUtils;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author HP
 */
public class PreTestInternet {

    private static final Logger API_LOGGER = LoggerFactory.getLogger("internet-test");

    private static final String URL1 = "https://google.com";

    private static final String URL2 = "https://youtube.com";

    public static void requestGoogle() {
        try {
            HttpResMsg resMsg1 = HttpclientUtils.get(URL1);
            API_LOGGER.info("为了验证做的并发访问google url：{}，response：{}", URL1, JSON.toJSONString(resMsg1));
            HttpResMsg resMsg2 = HttpclientUtils.get(URL2);
            API_LOGGER.info("为了验证做的并发访问youtube url：{}，response：{}", URL2, JSON.toJSONString(resMsg2));
        } catch (IOException e) {
            API_LOGGER.error(ExceptionUtil.getStackTraceAsString(e));
        }
    }
}
