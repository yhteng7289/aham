package com.pivot.aham.common.config.rabbit;

import com.pivot.aham.common.core.support.context.ApplicationContextHolder;

public class RabbitMqMessageHanlder {
    /**
     * 消息确认送达
     * @param messageId
     */
    public void confirmMessage(String messageId){
        //获取消息类型
        String messageType = messageId.split("#")[0];
        //获取对应消息类型处理类
        RabbitMessageAbstractHanlder rabbitMessageAbstractHanlder =
                (RabbitMessageAbstractHanlder) ApplicationContextHolder.getBean(messageType+"MessageHandler");
        rabbitMessageAbstractHanlder.confirmMessage(messageId);
    }


    /**
     * 消息
     * @param messageId
     */
    public void returnMessage(String messageId){
        //获取消息类型
        String messageType = messageId.split("#")[0];
        //获取对应消息类型处理类
        RabbitMessageAbstractHanlder rabbitMessageAbstractHanlder =
                (RabbitMessageAbstractHanlder) ApplicationContextHolder.getBean(messageType+"MessageHandler");
        rabbitMessageAbstractHanlder.returnMessage(messageId);
    }
}
