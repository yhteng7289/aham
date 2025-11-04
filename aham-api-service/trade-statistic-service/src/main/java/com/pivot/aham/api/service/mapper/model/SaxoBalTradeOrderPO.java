package com.pivot.aham.api.service.mapper.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@Accessors(chain = true)
public class SaxoBalTradeOrderPO implements Serializable {
    private Long id;
    private String productCode;
    private BigDecimal saxoTradeShare;
    private BigDecimal dasTradeShare;
    private BigDecimal saxoCommission;
    private BigDecimal dasCommission;
    private String statusDes;
    private String fileName;
    private Long orderNumber;
    private String transNumber;
    private String compareTime;

    public SaxoBalTradeOrderPO() {
    }

    public SaxoBalTradeOrderPO(String compareTime) {
        this.compareTime = compareTime;
    }
}
