package com.pivot.aham.common.core.support.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Activate(group = {Constants.PROVIDER})
public class DubboServiceFilter implements Filter {

    private Counter requestTotal = (Counter) ApplicationContextHolder.getBean("requestTotal");
    private Counter requestError = (Counter) ApplicationContextHolder.getBean("requestError");
    private Summary responseSummary = (Summary) ApplicationContextHolder.getBean("responseSummary");

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {
        Result result = null;
        Long takeTime = 0L;
        String name = invocation.getInvoker().getInterface().getName() + "." + invocation.getMethodName();
        String activeProfile = ApplicationContextHolder.getActiveProfile();
        requestTotal.labels(name, activeProfile).inc();
        Summary.Timer requestTimer = responseSummary.labels(name, activeProfile).startTimer();
        try {
//            Long startTime = System.currentTimeMillis();
            result = invoker.invoke(invocation);
            if (result.hasException()) {
//                throw new Exception(result.getException());
                requestError.labels(result.getException().getClass().getName(), name, activeProfile).inc();
                log.error("远程服务" + invocation.getInvoker().getInterface().getName() + "." + invocation.getMethodName() + "异常", result.getException());
                result = new RpcResult(RpcMessage.error(result.getException().getMessage()));
                return result;
            }
//            takeTime = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            requestError.labels(e.getClass().getName(), name, activeProfile).inc();
            log.error("远程服务" + invocation.getInvoker().getInterface().getName() + "." + invocation.getMethodName() + "异常", e);
            result = new RpcResult(RpcMessage.error(e.getMessage()));
            return result;
        } finally {
            log.info("远程服务:[{}],入参:{},出参:{},耗时:{} ms",
                    invocation.getMethodName(), invocation.getArguments(), JSON.toJSONString(result),
                    takeTime);
            requestTimer.observeDuration();
        }
        return result;
    }

}
