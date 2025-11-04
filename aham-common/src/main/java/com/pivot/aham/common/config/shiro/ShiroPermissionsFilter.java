package com.pivot.aham.common.config.shiro;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.MessageStandardCode;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class ShiroPermissionsFilter extends PermissionsAuthorizationFilter {
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Message fail = Message.error(MessageStandardCode.FORBIDDEN, MessageStandardCode.UNAUTHORIZED.msg());
        String json = JSON.toJSONString(fail);
        ShiroUserFilter.writeAndClose(response, json, MediaType.APPLICATION_JSON_UTF8_VALUE);
        return false;
    }
}
