package com.pivot.aham.api.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.req.ReceivedTransferReq;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferItem;
import com.pivot.aham.api.server.mq.message.RechargeRefundDTO;
import com.pivot.aham.api.server.mq.message.RefundMessageDTO;
import com.pivot.aham.api.server.remoteservice.UobTradeRemoteService;
import com.pivot.aham.api.service.bean.RechargeRefundBean;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccount;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrderMsgPO;
import com.pivot.aham.api.service.service.BankVirtualAccountOrderMsgService;
import com.pivot.aham.api.service.service.BankVirtualAccountService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.MsgStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import javax.annotation.Resource;
import java.util.List;

import static com.pivot.aham.common.core.Constants.MqConstants.REFUND_EXCHANGE;

/**
 * 退款邮件补偿任务
 */
/*@ElasticJobConf(name = "BankRefundGuaranteeJob_2",
        cron = "0 30 13 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "退款邮件补偿任务")
//@Component
@Slf4j
public class BankRefundGuaranteeJobImpl implements SimpleJob {

    @Resource
    private BankVirtualAccountOrderMsgService bankVirtualAccountOrderMsgService;
    @Resource
    private UobTradeRemoteService uobTradeRemoteService;
    @Resource
    private BankVirtualAccountService bankVirtualAccountService;
    @Resource
    private RabbitTemplate pubConfirmTemplate;


    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("#######退款邮件补偿任务,开始。");
        //检查退款消息表
        BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgQuery = new BankVirtualAccountOrderMsgPO();

        List<MsgStatusEnum> msgStatusEnumList = Lists.newArrayList(MsgStatusEnum.CREATE,MsgStatusEnum.EXCHANGE_FAILED);
        bankVirtualAccountOrderMsgQuery.setMsgStatusEnumList(msgStatusEnumList);
        List<BankVirtualAccountOrderMsgPO> bankVirtualAccountOrderMsgPOList =
                bankVirtualAccountOrderMsgService.queryBankVirtualAccountOrderMsgList(bankVirtualAccountOrderMsgQuery);

        log.info("未完成的退款消息:{}",JSON.toJSONString(bankVirtualAccountOrderMsgPOList));

        for(BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsg:bankVirtualAccountOrderMsgPOList){
            try {
                List<RechargeRefundBean> needRefundUsers = Lists.newArrayList();
                String[] bankOrderNos = bankVirtualAccountOrderMsg.getBankOrderNo().split(",");
                for (String bankOrderNo : bankOrderNos) {
                    //组装消息
                    ReceivedTransferReq receivedTransferReq = new ReceivedTransferReq();
                    receivedTransferReq.setBankOrderNo(bankOrderNo);
                    RpcMessage<ReceivedTransferItem> rpcMessage = uobTradeRemoteService.queryByBankOrderNo(receivedTransferReq);
                    ReceivedTransferItem receivedTransfer = rpcMessage.getContent();

                    BankVirtualAccount queryParam = new BankVirtualAccount();
                    queryParam.setVirtualAccountNo(receivedTransfer.getVirtualAccountNo());
                    BankVirtualAccount bankVirtualAccount = bankVirtualAccountService.quaryBankVirtualAccount(queryParam);

                    RechargeRefundBean rechargeRefund = new RechargeRefundBean();
                    rechargeRefund.setAmount(receivedTransfer.getCashAmount());
                    rechargeRefund.setBankOrderNumber(receivedTransfer.getBankOrderNo());
                    rechargeRefund.setBankProvidedName(receivedTransfer.getClientName());
                    rechargeRefund.setClientId(bankVirtualAccount.getClientId());
                    rechargeRefund.setClientName(bankVirtualAccount.getClientName());
                    rechargeRefund.setCurrency(receivedTransfer.getCurrency().getCode());
                    rechargeRefund.setTradeTime(DateUtils.formatDate(receivedTransfer.getTradeTime(), DateUtils.DATE_FORMAT4));
                    rechargeRefund.setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo());


                    needRefundUsers.add(rechargeRefund);
                }
                //发送消息
                RefundMessageDTO refundMessageBean = new RefundMessageDTO();
                List<RechargeRefundDTO> rechargeRefundDTOS = BeanMapperUtils.mapList(needRefundUsers, RechargeRefundDTO.class);
                refundMessageBean.setNeedRefundUsers(rechargeRefundDTOS);

                //再发送mq
                pubConfirmTemplate.convertAndSend(PropertiesUtil.getString(REFUND_EXCHANGE), "", refundMessageBean, message -> {
                    message.getMessageProperties().setMessageId(bankVirtualAccountOrderMsg.getMessageId());
                    return message;
                }, new CorrelationData(bankVirtualAccountOrderMsg.getMessageId()));
                log.info("退款订单,mq发送完毕:{}", JSON.toJSONString(refundMessageBean));
            }catch (Exception e){
                log.error("重发消息异常",e);
            }
        }
        log.info("#######退款邮件补偿任务,完成。");
    }
}*/
