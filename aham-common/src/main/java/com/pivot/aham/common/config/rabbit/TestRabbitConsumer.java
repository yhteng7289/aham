//package com.pivot.aham.common.config.rabbit;
//
//import com.pivot.aham.api.server.dto.AccountNavDTO;
//import com.rabbitmq.client.Channel;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
///**
// * 指定人工确认的监听工厂，rabbitManualListenerContainerFactory
// * 自动确认的监听工厂，rabbitListenerContainerFactory
// */
//@Component
//@Slf4j
//@RabbitListener(queues = "test01",containerFactory = "rabbitManualListenerContainerFactory")
//public class TestRabbitConsumer {
//
//
//    /**
//     * 根据消息类型消费消息
//     * @param message 消息体
//     * @param deliveryTag 消息标示
//     * @param redelivered 是否重投递
//     * @param channel 连接通道
//     * @throws Exception
//     */
//    @RabbitHandler
//    public void onMessage(@Payload AccountNavDTO message,
//                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
//                          @Header(AmqpHeaders.REDELIVERED) Boolean redelivered,
//                          Channel channel) throws Exception {
//        log.info("consume message = {} , deliveryTag = {} , redelivered = {}"
//                , message, deliveryTag, redelivered);
//        // 代表消费者确认收到当前消息，第二个参数表示一次是否 ack 多条消息
////        channel.basicAck(deliveryTag, false);
//
//        // 代表消费者拒绝一条或者多条消息，第二个参数表示一次是否拒绝多条消息，第三个参数表示是否把当前消息重新入队
////        channel.basicNack(deliveryTag, false, false);
//
//        // 代表消费者拒绝当前消息，第二个参数表示是否把当前消息重新入队，需要设置重试次数
////        channel.basicReject(deliveryTag,false);
//
//    }
//
//    @RabbitHandler
//    public void onMessage(@Payload String message,
//                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
//                          @Header(AmqpHeaders.REDELIVERED) Boolean redelivered,
//                          Channel channel) throws Exception {
//        log.info("consume message = {} , deliveryTag = {} , redelivered = {}"
//                ,message, deliveryTag, redelivered);
//
//        // 代表消费者确认收到当前消息，第二个参数表示一次是否 ack 多条消息
////        channel.basicAck(deliveryTag, false);
//
//        // 代表消费者拒绝一条或者多条消息，第二个参数表示一次是否拒绝多条消息，第三个参数表示是否把当前消息重新入队
////        channel.basicNack(deliveryTag, false, false);
//
//        // 代表消费者拒绝当前消息，第二个参数表示是否把当前消息重新入队，需要设置重试次数
////        channel.basicReject(deliveryTag,true);
//
//    }
//}
