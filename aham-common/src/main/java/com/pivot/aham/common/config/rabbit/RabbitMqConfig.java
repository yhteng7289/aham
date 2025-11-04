package com.pivot.aham.common.config.rabbit;

import com.pivot.aham.common.core.util.PropertiesUtil;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq配置
 * @author addison
 * @date: 2019/7/11
 */
@Configuration
public class RabbitMqConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        String host = PropertiesUtil.getString("rabbit.host");
        Integer port = PropertiesUtil.getInt("rabbit.port");
        String username = PropertiesUtil.getString("rabbit.username");
        String password = PropertiesUtil.getString("rabbit.password");
        String virtualHost = PropertiesUtil.getString("rabbit.virtualHost");

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean(value = "pubConfirmTemplate")
    public RabbitTemplate pubConfirmTemplate(
            ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(ack) {
                //发送成功
                String messageId = correlationData.getId();
                //发送给消息处理类(策略)
                RabbitMqMessageHanlder rabbitMqMessageHanlder = new RabbitMqMessageHanlder();
                rabbitMqMessageHanlder.confirmMessage(messageId);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            //没找到目标队列,发送失败
            String messageId = message.getMessageProperties().getMessageId();
            //发送给消息处理类(策略)
            RabbitMqMessageHanlder rabbitMqMessageHanlder = new RabbitMqMessageHanlder();
            rabbitMqMessageHanlder.returnMessage(messageId);
        });
        return rabbitTemplate;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }



    @Bean
    public MessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }

}