package com.pivot.aham.common.config.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestMessageHandler extends RabbitMessageAbstractHanlder {
    @Override
    public void confirmMessage(String messageId) {
        log.info("rabbit确认信息===="+messageId);

    }

    @Override
    public void returnMessage(String messageId) {
        log.info("rabbit未找到队列信息===="+messageId);
    }
}
