package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.RedeemTypeEnum;
import com.pivot.aham.common.enums.analysis.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月11日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_redeem_apply",resultMap = "RedeemApplyRes")
public class RedeemApplyPO extends BaseModel{
    /**
     * 源账户类型
     */
    private CurrencyEnum sourceAccountType;

    /**
     * 源币种金额
     */
    private BigDecimal sourceApplyMoney;

    /**
     * 申请金额
     */
    private BigDecimal applyMoney;
    /**
     * 目标币种
     */
    private CurrencyEnum targetCurrency;
    /**
     * clientId
     */
    @ExcelField(title = "Client Id")
    private String clientId;
    /**
     * 账户id
     */
    private Long accountId;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行账户
     */
    private String bankAccountNo;
    /**
     * 购汇金额
     */
    private BigDecimal exchangeAmount;
    /**
     * 提现来源类型
     */
    private WithdrawalSourceTypeEnum withdrawalSourceType;
    /**
     * 提现目标类型
     */
    private WithdrawalTargetTypeEnum withdrawalTargetType;
    /**
     * 目标银行类型
     */
    private WithdrawalTargetBankTypeEnum withdrawalTargetBankType;
    /**
     * 海外银行辅助字段
     */
    private String swift;
    private String branch;
    private String bankAddress;

    /**
     * 提现整体处理状态
     */
    private RedeemApplyStatusEnum redeemApplyStatus;

    /**
     * SAXO交易状态
     */
    private EtfExecutedStatusEnum etfExecutedStatus;
    /**
     * SAXOTOUOB转账状态
     */
    private SaxoToUobTransferStatusEnum saxoToUobTransferStatus;
    /**
     * uob划款状态
     */
    private BankTransferStatusEnum bankTransferStatus;

    private Long bankTransferOrderId;
    /**
     * 确认提现金额
     */
    @ExcelField(title = "Amount to payout")
    private BigDecimal confirmAmount;
    /**
     * 确认提现金额（saxo内部购汇后）
     */
    private BigDecimal confirmAmountInSgd;
    /**
     * 确认舍弃的提现金额
     */
    private BigDecimal confirmAbandonAmount;

    /**
     * 申请时间
     */
    private Date applyTime;
    /**
     * 确认时间
     */
    private Date confirmTime;
    /**
     * 零食订单
     */
    private Long totalTmpOrderId;

    /**
     * 提现的目标id
     */
    @ExcelField(title = "Goal Id")
    private String goalId;
    
     /**
     * 线下处理批次id
     */
    @ExcelField(title = "Batch ID")
    private String saxoToUobBatchId;

    /**
     * 提现类型
     */
    private RedeemTypeEnum redeemType;

    /**
     * 查询辅助
     */
    private Date startApplyTime;
    private Date endApplyTime;
    
    private Date startConfirmTime;
    private Date endConfirmTime;

}
