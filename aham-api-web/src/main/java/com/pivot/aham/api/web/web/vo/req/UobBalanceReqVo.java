/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.web.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author HP
 */
@Data
@Accessors(chain = true)
public class UobBalanceReqVo {

    private String odDrawingLimit;
    private String accountName;
    private String subAccountAllocatedBalance;
    private String todayDebit;
    private String samcPrimaryAccountIndicator;
    private String accountType;
    private String accountCurrency;
    private String accountNumber;
    private String branch;
    private String masterAccountNumberForSubAccount;
    private String totalAvailabilityFloat;
    private String todayCredit;
    private String availableBalanceAmount;
    private String availableBalanceCurrency;
    private String ledgerBalanceAmount;
    private String ledgerBalanceCurrency;
    private String accountBalanceObjAmount;
    private String accountBalanceObjCurrency;

}
