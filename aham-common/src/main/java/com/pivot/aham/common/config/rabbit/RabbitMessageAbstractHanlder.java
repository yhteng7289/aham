package com.pivot.aham.common.config.rabbit;

public abstract class RabbitMessageAbstractHanlder {
    /**
     * 消息确认
     * @param messageId
     */
    public abstract void confirmMessage(String messageId);

    /**
     * 消息已发送到exchange，但未路由到queue
     * @param messageId
     */
    public abstract void returnMessage(String messageId);
}
