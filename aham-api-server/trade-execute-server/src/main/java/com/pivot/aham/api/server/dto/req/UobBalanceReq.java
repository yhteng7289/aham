/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author HP
 */
@Data
@Accessors(chain = true)
public class UobBalanceReq extends BaseDTO {

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
