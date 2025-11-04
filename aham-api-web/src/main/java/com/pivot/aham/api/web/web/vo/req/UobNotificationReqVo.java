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
public class UobNotificationReqVo {

    private String event;
    private String accountName;
    private String accountType;
    private String accountNumber;
    private String accountCurrency;
    private String amount;
    private String transactionType;
    private String ourReference;
    private String yourReference;
    private String transactionText;
    private String transactionDateTime;
    private String businessDate;

    private String effectiveDate;
    private String subAccountIndicator;
    private String payNowIndicator;
    private String instructionId;
    private String notificationId;
    private String remittanceInformation;
    private String originatorAccountName;

}
