package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
public class UobNotificationPO extends BaseModel {

    private String event;
    private String accountName;
    private String accountType;
    private String accountNumber;
    private String accountCurrency;
    private BigDecimal amount;
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
