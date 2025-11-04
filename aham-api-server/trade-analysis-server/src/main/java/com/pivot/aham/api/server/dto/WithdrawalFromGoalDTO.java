package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetBankTypeEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月10日
 */
@Data
@Accessors(chain = true)
public class WithdrawalFromGoalDTO extends BaseDTO{
    /**
     * 申请币种（不要理解为账户币种类型）
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
    private String clientId;
    /**
     * goalId
     */
    private String goalId;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行账户
     */
    private String bankAccountNo;
    /**
     * 提现目标账户类型
     */
    private WithdrawalTargetTypeEnum withdrawalTargetType;
    /**
     * 目标银行类型
     */
    private WithdrawalTargetBankTypeEnum withdrawalTargetBankType;
    private String swift;
    private String branch;
    private String bankAddress;
    private Date exchangeRateDate;

}
