package com.pivot.aham.common.config.shiro;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.MessageStandardCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.http.MediaType;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Slf4j
public class ShiroUserFilter extends UserFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        Message fail = Message.error(MessageStandardCode.UNAUTHORIZED, MessageStandardCode.UNAUTHORIZED.msg());
        String json = JSON.toJSONString(fail);
        writeAndClose(response, json, MediaType.APPLICATION_JSON_UTF8_VALUE);

        return false;
    }

    public static void writeAndClose(ServletResponse response, String content, String contentType) {
        ServletOutputStream outputStream = null;
        response.setContentType(contentType);

        try {
            outputStream = response.getOutputStream();
            outputStream.write(content.getBytes());
        } catch (IOException var13) {
            log.error("写入响应内容失败", var13);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException var12) {
                    log.error("关闭响应流失败", var12);
                }
            }

        }

    }
}