package com.pivot.aham.common.config.requestmapping;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
 * 初始化设置返回值处理器
 *
 * @author addison
 * @since 2018年11月18日
 */
public class ReturnValueHandlerFactory implements InitializingBean {
    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        adapter.setReturnValueHandlers(
            decorateHandler(new ArrayList(adapter.getReturnValueHandlers())));
    }

    private List<HandlerMethodReturnValueHandler> decorateHandler(
        List<HandlerMethodReturnValueHandler> handlers) {
        for (HandlerMethodReturnValueHandler handler : handlers) {
            //定制requestBody的返回值
            if (handler instanceof RequestResponseBodyMethodProcessor) {
                handlers.set(handlers.indexOf(handler), new ReturnValueHandler(handler));
                break;
            }
        }
        return handlers;
    }
}
