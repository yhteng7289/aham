package com.pivot.aham.api.service.job.interevent;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 处理fee工厂类
 *
 * @author addison
 * @since 2019年01月22日
 */
@Configuration
@Slf4j
public class EventBusFactory {
    @Bean
    public EventBus eventBus(){
        EventBus eventBus = new EventBus((exception, context) -> log.error(context.getSubscriber()+"事件执行异常",exception));
        return eventBus;
    }
}
