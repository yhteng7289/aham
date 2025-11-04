package com.pivot.aham.api.service.mapper.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Accessors(chain = true)
public class SaxoAccountStatusPO implements Serializable {
    private Long id;
    private String account;
    private Date date;
    private String accountCurrency;
    private BigDecimal balance;
    private BigDecimal openPositionsFX;
    private BigDecimal openPositionsFXOptions;
    private BigDecimal openPositionsStock;
    private BigDecimal openPositionsCFD;
    private BigDecimal openPositionsCFDOnOption;
    private BigDecimal openPositionsFutures;
    private BigDecimal openPositionsETO;
    private BigDecimal openPositionsMutualFunds;
    private BigDecimal openPositionsBonds;
    private BigDecimal reservedForFutureInstrument2;
    private String activeAccount;
    private BigDecimal reservedForFutureUse;
    private BigDecimal totalEquity;
    private BigDecimal accountFunding;
    private BigDecimal ReservedForFutureUse2;
    private String partnerAccountKey;
    private BigDecimal valueDateCashBalance;
    private BigDecimal marginFOrTrading;
    private Long riskGroupProfile;
    private String accountRiskProfile;
    private String accountLevelMargining;
    private Long counterpartID;
    private BigDecimal otherCollateral;
    private BigDecimal notAvailableAsMarginCollateral;
    private Long ownerID;
    private String isClientAccount;
    private Date createDate;

    public SaxoAccountStatusPO() {
    }

    public SaxoAccountStatusPO(String account, Date createDate) {
        this.account = account;
        this.createDate = createDate;
    }
}
