package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class BankVirtualAccountOrderBalDTO extends BaseDTO {
    /**
     * 虚拟账户
     */
    @ExcelField(title = "virtual_account_no")
    private String virtualAccountNo;
    /**
     * 交易金额
     */
    @ExcelField(title = "cash_amount")
    private BigDecimal cashAmount;
    /**
     * 账户类型：1:新币账户,2:美金账户
     */
    @ExcelField(title = "currency")
    private String currency;

    /**
     * 交易状态
     */
    @ExcelField(title = "order_status")
    private String orderStatus;
    /**
     * 交易类型
     */
    @ExcelField(title = "operator_type")
    private String operatorType;
    /**
     * 是否需要退款
     */
    @ExcelField(title = "need_refend_type")
    private String needRefundType;
    /**
     * 银行订单号
     */
    @ExcelField(title = "bank_order_no")
    private String bankOrderNo;

    /**
     * 交易时间
     */
    @ExcelField(title = "trade_time")
    private String tradeTime;

    /**
     * 投资目标关联标识
     */
    @ExcelField(title = "reference_code")
    private String referenceCode;

    /**
     * 提现申请id
     */
    @ExcelField(title = "redeem_apply_id")
    private Long redeemApplyId;

    /**
     * 动作类型
     */
    @ExcelField(title = "action_type")
    private String actionType;
}
