package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.remoteservice.TestExcuteRemoteService;
import com.pivot.aham.api.service.impl.trade.Finish;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 用户提现操作
 *
 * @author addison
 * @since 2018年12月10日
 */
@Service(interfaceClass = TestExcuteRemoteService.class)
@Slf4j
public class TestExcuteRemoteServiceImpl implements TestExcuteRemoteService {

    @Autowired
    private Finish finish;

    @Override
    public void finishNotify() {
        log.info("开始执行 =======>>> FinishNotifyJob");

        try {
            finish.finishNotify();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
        log.info("执行结束 =======>>> FinishNotifyJob");
    }
}
