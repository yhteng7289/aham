package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetBankTypeEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月10日
 */
@Data
@Accessors(chain = true)
public class WithdrawalFromVirtalAccountDTO extends BaseVo {
    /**
     * 源账户类型
     */
    private CurrencyEnum sourceAccountType;
    /**
     * 申请金额
     */
    private BigDecimal applyAmount;
    /**
     * 目标币种
     */
    private CurrencyEnum targetCurrency;
    /**
     * clientId
     */
    private String clientId;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行账号
     */
    private String bankAccountNo;
    /**
     * 提现目标账户类型
     */
    private WithdrawalTargetTypeEnum withdrawalTargetType;
    /**
     * 提现目标银行类型
     */
    private WithdrawalTargetBankTypeEnum withdrawalTargetBankType;
    /**
     * 海外银行需要以下两个字段
     */
    private String swift;
    private String branch;
    private String bankAddress;

}
