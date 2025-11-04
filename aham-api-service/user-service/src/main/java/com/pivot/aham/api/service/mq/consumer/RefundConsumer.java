package com.pivot.aham.api.service.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.mq.message.RechargeRefundDTO;
import com.pivot.aham.api.server.mq.message.RefundMessageDTO;
import com.pivot.aham.api.service.bean.RechargeRefundBean;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrderMsgPO;
import com.pivot.aham.api.service.service.BankVirtualAccountOrderMsgService;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.MsgStatusEnum;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 退款消息消费处理
 */
@Component
@Slf4j
@RabbitListener(queues = "${rabbitmq.refund_queue1}", containerFactory = "rabbitManualListenerContainerFactory")
public class RefundConsumer {
    @Resource
    private BankVirtualAccountOrderMsgService bankVirtualAccountOrderMsgService;
    @Resource
    private RedissonHelper redissonHelper;


    /**
     * 一天的秒数
     */
    private final static int ONE_DAY_SECONDS = 24 * 3600;
    /**
     * 重试次数=设定次数-1
     */
    private final static int TRYTIMES = 3;

    /**
     * 消费退款消息
     *
     * @param refundMessageBean 消息体
     * @param deliveryTag       消息标示
     * @param redelivered       是否重投递
     * @param channel           连接通道
     * @throws Exception
     */
    @RabbitHandler
    public void confirmMessage(@Payload RefundMessageDTO refundMessageBean,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                               @Header(AmqpHeaders.REDELIVERED) Boolean redelivered,
                               @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                               Channel channel) throws Exception {
        log.info("bankRefund consume message = {} , deliveryTag = {} , redelivered = {}"
                , refundMessageBean, deliveryTag, redelivered);

        if (refundMessageBean == null) {
            channel.basicAck(deliveryTag, false);
            throw new BusinessException("消息非法,消息为空");
        }
//        BankVirtualAccountOrderMsgDTO bankVirtualAccountOrderMsgDto = refundMessageBean.getBankVirtualAccountOrderMsgPO();
        List<RechargeRefundDTO> needRefundUsers = refundMessageBean.getNeedRefundUsers();

        //根据messageId查询退款消息记录
        if (messageId == null) {
            channel.basicAck(deliveryTag, false);
            throw new BusinessException("消息非法,没有messageId");
        }

        try {
            //幂等校验
            BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgQuery = new BankVirtualAccountOrderMsgPO();
            bankVirtualAccountOrderMsgQuery.setMessageId(messageId);
            BankVirtualAccountOrderMsgPO bankVirtualAccountOrder = bankVirtualAccountOrderMsgService.selectOne(bankVirtualAccountOrderMsgQuery);

            if (bankVirtualAccountOrder == null) {
                channel.basicReject(deliveryTag, false);
                log.error("找不到消息记录:{}", JSON.toJSONString(refundMessageBean));
                return;
            }

            //为了处理消费者比ack确认快，如果消息提起到达说明肯定会ack
            if (bankVirtualAccountOrder.getMsgStatus() != MsgStatusEnum.SENDED) {
                log.error("该消息前置状态错误或找不到消息记录:{}", JSON.toJSONString(refundMessageBean));
                bankVirtualAccountOrder.setMsgStatus(MsgStatusEnum.SENDED);
                bankVirtualAccountOrderMsgService.updateOrInsert(bankVirtualAccountOrder);
            }

            //发送邮件
            sendRefMail(needRefundUsers);

            //更新消息状态
            BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgUpdate = new BankVirtualAccountOrderMsgPO();
            bankVirtualAccountOrderMsgUpdate.setMessageId(messageId);
            bankVirtualAccountOrderMsgUpdate.setPreMsgStatus(MsgStatusEnum.SENDED);
            bankVirtualAccountOrderMsgUpdate.setMsgStatus(MsgStatusEnum.CONSUMERED);
            bankVirtualAccountOrderMsgUpdate.setConsumerCompleteTime(DateUtils.now());
            bankVirtualAccountOrderMsgService.updateByMessageId(bankVirtualAccountOrderMsgUpdate);

//            throw new BusinessException("异常测试");
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("更新消息状态失败,重新放入队列", e);
            redissonHelper.setnx(messageId, 0, ONE_DAY_SECONDS);
            Long tryTimes = redissonHelper.incr(messageId);
            if (tryTimes > TRYTIMES) {
                log.error("重试次数已达到3次:{}", messageId);
                channel.basicReject(deliveryTag, false);
            } else {
                channel.basicReject(deliveryTag, true);
            }

            BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgUpdate = new BankVirtualAccountOrderMsgPO();
            bankVirtualAccountOrderMsgUpdate.setMessageId(messageId);
            bankVirtualAccountOrderMsgUpdate.setPreMsgStatus(MsgStatusEnum.SENDED);
            bankVirtualAccountOrderMsgUpdate.setMsgStatus(MsgStatusEnum.CONSUMERFAIL);
            bankVirtualAccountOrderMsgUpdate.setConsumerCompleteTime(DateUtils.now());
            bankVirtualAccountOrderMsgService.updateByMessageId(bankVirtualAccountOrderMsgUpdate);
        }


    }


    /**
     * 发送邮件
     *
     * @param needRefundUsers
     * @throws MessagingException
     * @throws IOException
     */
    private void sendRefMail(List<RechargeRefundDTO> needRefundUsers) throws MessagingException, IOException {
        log.info("充值用户退款,date:{}", DateUtils.getDate());
        ExportExcel exportExcel = new ExportExcel(null, RechargeRefundBean.class);
        exportExcel.setDataList(needRefundUsers);
        String fileName = "recharge_refund.xlsx";
        String topic = "recharge_need_refund_" + DateUtils.getDate();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        exportExcel.write(os);

        BodyPart bodyPart = new MimeBodyPart();
        ByteArrayDataSource dataSource = new ByteArrayDataSource(os.toByteArray(), "application/png");
        bodyPart.setDataHandler(new DataHandler(dataSource));
        bodyPart.setFileName(fileName);

        Email email = new Email();
        email.setBodyPart(bodyPart);
        email.setSendTo(PropertiesUtil.getString("email.recharge.refund"));
        email.setTopic(topic);
        email.setBody(DateUtils.getDate() + ",充值退款看附件");

        EmailUtil.sendEmail(email);
        exportExcel.dispose();
        log.info("充值用户退款成功,date:{}", DateUtils.getDate());
    }

}
