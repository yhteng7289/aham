package com.pivot.aham.api.service.mapper.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class SaxoShareTradesExecutedPO implements Serializable {
    private Long id;
    //1
    private Date reportingDate;
    //2
    private String instrumentType;
    //3
    private Long counterpartID;
    //4
    private String counterpartName;
    //5
    private String accountNumber;
    //7
    private String accountCurrency;
//    8
    private String instrumentCode;
//    9
    private String instrumentDescription;
//  13
    private BigDecimal tradedAmount;
//   16
    private Long orderNumber;
//    17
    private Date tradeTime;
//    18
    private Date tradeDate;
//    20
    private String buySell;
//    21
    private BigDecimal price;
//    23
    private BigDecimal commissionInstrumentCurrency;
   //28
    private String tradeType;

    private Date createDate;

    public SaxoShareTradesExecutedPO(Date createDate) {
        this.createDate = createDate;
    }

    public SaxoShareTradesExecutedPO() {
    }
}
