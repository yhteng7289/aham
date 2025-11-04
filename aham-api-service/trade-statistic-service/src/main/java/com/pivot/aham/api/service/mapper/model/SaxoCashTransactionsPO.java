package com.pivot.aham.api.service.mapper.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class SaxoCashTransactionsPO implements Serializable {
    private Long id;
    private String account;
    private String accountCurrency;
    private Long transactionNumber;
    private Long orderNumber;
    private Long frontOfficeLinkID;
    private Date date;
    private Date valueDate;
    private BigDecimal grossAmountCashCurrency;
    private String cashCurrency;
    private BigDecimal feeCashCurrency;
    private BigDecimal cashToAccountRate;
    private BigDecimal grossAmountAccountCurrency;
    private BigDecimal feeAccountCurrency;
    private BigDecimal netAmountAccountCurrency;
    private String partnerAccountKey;
    private String comment;
    private Long counterpartID;
}
