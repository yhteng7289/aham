package com.pivot.aham.common.config.requestmapping;

import com.pivot.aham.common.core.base.Message;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * requestmapping全局返回值
 *
 * @author addison
 * @since 2018年11月18日
 */
public class ReturnValueHandler implements HandlerMethodReturnValueHandler {
    private HandlerMethodReturnValueHandler handler;

    public ReturnValueHandler(HandlerMethodReturnValueHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return handler.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        String path =  webRequest.getNativeRequest(HttpServletRequest.class).getServletPath();
        if (returnValue != null) {
            if (returnValue instanceof Message || returnValue instanceof ResponseEntity || path.contains("actuator")) {
                handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            } else {
                Message message = Message.success();
                message.setContent(returnValue);
                handler.handleReturnValue(message, returnType, mavContainer, webRequest);
            }
            //为空默认处理
        } else {
            handler.handleReturnValue(Message.success(), returnType, mavContainer, webRequest);
        }
    }
}
