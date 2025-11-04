package com.pivot.aham.api.service.mapper.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Accessors(chain = true)
public class SaxoBookkeepingCashPO implements Serializable {
    private Long id;
    private Date reportingDate;
    private String instrumentType;
    private Long counterpartID;
    private String counterpartName;
    private String accountNumber;
    private String partnerAccountKey;
    private String accountCurrency;
    private String instrumentCode;
    private String instrumentCurrency;
    private BigDecimal amountInstrumentCurreny;
    private Long bkAmountID;
    private Date tradeDate;
    private Long relatedPositionID;
    private Long relatedTradeId;
    private Long orderNumber;
    private Long bkAmountTypeID;
    private String bkAmountType;
    private Date valueDate;
    private BigDecimal amountAccountCurrency;
    private BigDecimal instrumentToAccountRate;
    private Long originalTradeID;
    private String correctionLeg;
    private Long caEventTypeID;
    private String caEventTypeName;
    private Long adhocBookingCodeID;
    private String adhocBookingCode;
    private Long instrumentUIC;
    private Date actualTradeDate;
    private String caEventID;
    private Date createDate;

    public SaxoBookkeepingCashPO() {
    }

    public SaxoBookkeepingCashPO(Date createDate) {
        this.createDate = createDate;
    }
}
