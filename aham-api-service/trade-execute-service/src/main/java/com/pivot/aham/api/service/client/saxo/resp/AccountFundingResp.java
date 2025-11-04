package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountFundingResp {
    private String AccountId;
    private String ActivityTime;
    private String ActivityType;
    private BigDecimal Amount;
    private String ClientId;
    private BigDecimal ConversionRate;
    private String CurrencyCode;
    private String FundingEvent;
    private String FundingType;
    private String PositionId;
    private String RegistrationTime;
    private String SequenceId;
    private String ValueDate;
    private Boolean confirmed;
}
