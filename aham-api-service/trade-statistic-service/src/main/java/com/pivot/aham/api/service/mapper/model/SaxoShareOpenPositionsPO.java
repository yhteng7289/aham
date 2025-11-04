package com.pivot.aham.api.service.mapper.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class SaxoShareOpenPositionsPO implements Serializable {
    private Long id;
    private Date reportingDate;
    private String instrumentType;
    private Long counterpartID;
    private String counterpartName;
    private String accountNumber;
    private String partnerAccountKey;
    private String accountCurrency;
    private String instrument;
    private String description;
    private String instrumentCurrency;
    private String isInCode;
    private String exchangeDescription;
    private BigDecimal amount;
    private BigDecimal figureSize;
    private Long tradeNumber;
    private Long orderNumber;
    private Date tradeTime;
    private Date tradeDate;
    private Date valueDate;
    private String buySell;
    private BigDecimal price;
    private BigDecimal quotedValue;
    private BigDecimal eodRate;
    private BigDecimal instrumentToAccountTate;
    private String isPartial;
    private BigDecimal unrealisedValueInstrument;
    private BigDecimal unrealisedValueAccount;
    private String externalOrderID;
    private String previousIsInCode;
    private String dvp;
    private String exchangeIsoCode;
    private String isoMic;
    private Date createTime;

    public SaxoShareOpenPositionsPO(Date createTime) {
        this.createTime = createTime;
    }

    public SaxoShareOpenPositionsPO() {
    }
}
