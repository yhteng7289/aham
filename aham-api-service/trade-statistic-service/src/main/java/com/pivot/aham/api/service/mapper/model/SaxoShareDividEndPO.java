package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.TransferStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class SaxoShareDividEndPO implements Serializable {
    private Long id;
    private String instrumentType;
    private Long caEventId;
    private Date exDate;
    private Date tradeDate;
    private Date valueDate;
    private Long counterpartId;
    private String counterpartName;
    private String accountNumber;
    private String partnerAccountKey;
    private String accountCurrency;
    private Long tradeNumber;
    private String instrumentDesctiption;
    private String instrumentCode;
    private String isInCode;
    private String countryOfIssue;
    private BigDecimal figureSize;
    private String instrumentCurrency;
    private BigDecimal eligibleQuantity;
    private BigDecimal netAmountDividendCurrency;
    private BigDecimal dividendToAccountRate;
    private BigDecimal netAmountAccountCurrency;
    private BigDecimal withholdingTaxPercentage;
    private BigDecimal withholdingTaxAmountDividendCurrency;
    private BigDecimal grossAmountDividendCurrency;
    private String dividendCurrency;
    private BigDecimal frankedAmountDividendCurrency;
    private BigDecimal unFrankedAmountDividendCurrency;
    private BigDecimal frankingCreditDividendCurrency;
    private BigDecimal grossRate;
    private Long caEventTypeId;
    private String CaEventTypeName;
    private String exchangelSoCode;
    private String isoMic;
    private Long instrumentUic;
    private BigDecimal grossDividendAmountDividendCurrency;
    private BigDecimal interestAmountDividendCurrency;
    private BigDecimal residencyTaxPercentage;
    private BigDecimal residencyTaxAmountDividendCurrency;
    private BigDecimal euTaxPercentage;
    private BigDecimal euTaxAmountDividendCurrency;
    private BigDecimal feeAmountDividendCurrendcy;
    private TransferStatusEnum transferStatusEnum;
    private Date createDate;
    private Date updateDate;

    public SaxoShareDividEndPO(Date createDate) {
        this.createDate = createDate;
    }

    public SaxoShareDividEndPO() {
    }

    public SaxoShareDividEndPO(TransferStatusEnum transferStatusEnum) {
        this.transferStatusEnum = transferStatusEnum;
    }

    public SaxoShareDividEndPO(String instrumentType, Long caEventId, Date exDate, Date tradeDate, Date valueDate, Long counterpartId, String counterpartName, String accountNumber, String partnerAccountKey, String accountCurrency, Long tradeNumber, String instrumentDesctiption, String instrumentCode, String isInCode, String countryOfIssue, BigDecimal figureSize, String instrumentCurrency, BigDecimal eligibleQuantity, BigDecimal netAmountDividendCurrency, BigDecimal dividendToAccountRate, BigDecimal netAmountAccountCurrency, BigDecimal withholdingTaxPercentage, BigDecimal withholdingTaxAmountDividendCurrency, BigDecimal grossAmountDividendCurrency, String dividendCurrency, BigDecimal frankedAmountDividendCurrency, BigDecimal unFrankedAmountDividendCurrency, BigDecimal frankingCreditDividendCurrency, BigDecimal grossRate, Long caEventTypeId, String caEventTypeName, String exchangelSoCode, String isoMic, Long instrumentUic, BigDecimal grossDividendAmountDividendCurrency, BigDecimal interestAmountDividendCurrency, BigDecimal residencyTaxPercentage, BigDecimal residencyTaxAmountDividendCurrency, BigDecimal euTaxPercentage, BigDecimal euTaxAmountDividendCurrency, BigDecimal feeAmountDividendCurrendcy, TransferStatusEnum transferStatusEnum, Date createDate, Date updateDate) {
        this.instrumentType = instrumentType;
        this.caEventId = caEventId;
        this.exDate = exDate;
        this.tradeDate = tradeDate;
        this.valueDate = valueDate;
        this.counterpartId = counterpartId;
        this.counterpartName = counterpartName;
        this.accountNumber = accountNumber;
        this.partnerAccountKey = partnerAccountKey;
        this.accountCurrency = accountCurrency;
        this.tradeNumber = tradeNumber;
        this.instrumentDesctiption = instrumentDesctiption;
        this.instrumentCode = instrumentCode;
        this.isInCode = isInCode;
        this.countryOfIssue = countryOfIssue;
        this.figureSize = figureSize;
        this.instrumentCurrency = instrumentCurrency;
        this.eligibleQuantity = eligibleQuantity;
        this.netAmountDividendCurrency = netAmountDividendCurrency;
        this.dividendToAccountRate = dividendToAccountRate;
        this.netAmountAccountCurrency = netAmountAccountCurrency;
        this.withholdingTaxPercentage = withholdingTaxPercentage;
        this.withholdingTaxAmountDividendCurrency = withholdingTaxAmountDividendCurrency;
        this.grossAmountDividendCurrency = grossAmountDividendCurrency;
        this.dividendCurrency = dividendCurrency;
        this.frankedAmountDividendCurrency = frankedAmountDividendCurrency;
        this.unFrankedAmountDividendCurrency = unFrankedAmountDividendCurrency;
        this.frankingCreditDividendCurrency = frankingCreditDividendCurrency;
        this.grossRate = grossRate;
        this.caEventTypeId = caEventTypeId;
        CaEventTypeName = caEventTypeName;
        this.exchangelSoCode = exchangelSoCode;
        this.isoMic = isoMic;
        this.instrumentUic = instrumentUic;
        this.grossDividendAmountDividendCurrency = grossDividendAmountDividendCurrency;
        this.interestAmountDividendCurrency = interestAmountDividendCurrency;
        this.residencyTaxPercentage = residencyTaxPercentage;
        this.residencyTaxAmountDividendCurrency = residencyTaxAmountDividendCurrency;
        this.euTaxPercentage = euTaxPercentage;
        this.euTaxAmountDividendCurrency = euTaxAmountDividendCurrency;
        this.feeAmountDividendCurrendcy = feeAmountDividendCurrendcy;
        this.transferStatusEnum = transferStatusEnum;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
