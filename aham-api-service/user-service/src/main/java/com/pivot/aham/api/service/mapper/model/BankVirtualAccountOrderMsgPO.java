package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.api.service.bean.RechargeRefundBean;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.MsgStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 银行退款消息
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_bank_va_order_refend_msg",resultMap = "bankVirtualAccountOrderMsgRes")
public class BankVirtualAccountOrderMsgPO extends BaseModel {
    /**
     * 银行订单
     */
    private String bankOrderNo;
    /**
     * 消息状态
     */
    private MsgStatusEnum msgStatus;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 发送确认时间
     */
    private Date sendConfirmTime;
    /**
     * 消费完成时间
     */
    private Date consumerCompleteTime;
    /**
     * 消息id
     */
    private String messageId;


    private MsgStatusEnum preMsgStatus;


    /**
     * 退款消息体（传递消息用）
     */
    List<RechargeRefundBean> needRefundUsers;
    List<MsgStatusEnum> msgStatusEnumList;


}
