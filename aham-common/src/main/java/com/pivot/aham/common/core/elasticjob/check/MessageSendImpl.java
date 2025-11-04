package com.pivot.aham.common.core.elasticjob.check;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageSendImpl implements MessageSend {

    @Override
    public void send(String msg) {
//        ErrorLogAndMailUtil.logErrorForTrade(log, msg);
    }
}
