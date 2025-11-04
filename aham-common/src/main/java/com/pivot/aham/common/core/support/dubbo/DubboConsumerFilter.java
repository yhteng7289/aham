package com.pivot.aham.common.core.support.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.pivot.aham.common.core.exception.RemoteServiceException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Activate(group = {Constants.CONSUMER})
public class DubboConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {
        Result result = null;
        Long takeTime = 0L;
        try {
            Long startTime = System.currentTimeMillis();
            result = invoker.invoke(invocation);
            if (result.getException() instanceof Exception) {
                throw new Exception(result.getException());
            }
            takeTime = System.currentTimeMillis() - startTime;
        } catch (Exception e) {

//            log.error("调用远程服务"+invocation.getInvoker().getInterface().getName()+"."+invocation.getMethodName()+"异常", e);
            throw new RemoteServiceException("CC调用远程服务" + invocation.getInvoker().getInterface().getName() + "." + invocation.getMethodName() + "异常", e);
        } finally {
            log.info("调用远程服务:[{}],入参:{},出参:{},耗时:{} ms",
                    invocation.getMethodName(), invocation.getArguments(), JSON.toJSONString(result),
                    takeTime);
        }
        return result;
    }

}
