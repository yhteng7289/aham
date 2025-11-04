package com.pivot.aham.api.service.mq.consumer;

import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrderMsgPO;
import com.pivot.aham.api.service.service.BankVirtualAccountOrderMsgService;
import com.pivot.aham.common.config.rabbit.RabbitMessageAbstractHanlder;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.MsgStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class BankRefundMessageHandler extends RabbitMessageAbstractHanlder {
    @Resource
    private BankVirtualAccountOrderMsgService bankVirtualAccountOrderMsgService;
    @Override
    public void confirmMessage(String messageId) {
        //设置退款记录发送状态
        //根据messageId更新状态
        log.info("消息已确认到mq");
        BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgPO = new BankVirtualAccountOrderMsgPO();
        bankVirtualAccountOrderMsgPO.setMsgStatus(MsgStatusEnum.SENDED);
        bankVirtualAccountOrderMsgPO.setMessageId(messageId);
        bankVirtualAccountOrderMsgPO.setPreMsgStatus(MsgStatusEnum.CREATE);
        bankVirtualAccountOrderMsgPO.setSendConfirmTime(DateUtils.now());
        bankVirtualAccountOrderMsgService.updateByMessageId(bankVirtualAccountOrderMsgPO);

    }

    @Override
    public void returnMessage(String messageId) {
        //设置退款记录失败状态
        //根据messageId更新状态
        log.info("消息已确认到exchane，但是没到mq");
        BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgPO = new BankVirtualAccountOrderMsgPO();
        bankVirtualAccountOrderMsgPO.setMsgStatus(MsgStatusEnum.EXCHANGE_FAILED);
        bankVirtualAccountOrderMsgPO.setMessageId(messageId);
        bankVirtualAccountOrderMsgService.updateByMessageId(bankVirtualAccountOrderMsgPO);
    }
}
