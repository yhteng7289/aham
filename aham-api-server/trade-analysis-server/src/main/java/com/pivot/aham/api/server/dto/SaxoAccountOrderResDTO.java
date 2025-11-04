package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SaxoAccountOrderResDTO extends BaseDTO {
    /**
     * 发生金额
     */
    private BigDecimal cashAmount;
    /**
     * 币种
     */
    private CurrencyEnum currency;
    /**
     * 订单状态
     */
    private SaxoOrderTradeStatusEnum orderStatus;
    /**
     * 交易状态
     */
    private SaxoOrderTradeTypeEnum operatorType;
    /**
     * 交易来源
     */
    private SaxoOrderActionTypeEnum actionType;
    private Date tradeTime;
    /**
     * uob到saxo转账的订单管理
     */
    private Long exchangeOrderNo;
    /**
     * 线下入金时UOB的订单号
     */
    private String bankOrderNo;
    private String clientId;
    private Long accountId;
    private String goalId;

    /**
     * 提现流水关联
     */
    private Long redeemApplyId;
    /**
     * 转账订单号（批量）
     */
    private Long exchangeTotalOrderId;
}
