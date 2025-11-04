package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;
import lombok.experimental.Accessors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
@Accessors(chain = true)
public class UobBalancePO extends BaseModel {

    private BigDecimal odDrawingLimit;
    private String accountName;
    private BigDecimal subAccountAllocatedBalance;
    private BigDecimal todayDebit;
    private String samcPrimaryAccountIndicator;
    private String accountType;
    private String accountCurrency;
    private String accountNumber;
    private Integer branch;
    private String masterAccountNumberForSubAccount;
    private BigDecimal totalAvailabilityFloat;
    private BigDecimal todayCredit;
    private BigDecimal availableBalanceAmount;
    private String availableBalanceCurrency;
    private BigDecimal ledgerBalanceAmount;
    private String ledgerBalanceCurrency;
    private BigDecimal accountBalanceAmount;
    private String accountBalanceCurrency;
}
