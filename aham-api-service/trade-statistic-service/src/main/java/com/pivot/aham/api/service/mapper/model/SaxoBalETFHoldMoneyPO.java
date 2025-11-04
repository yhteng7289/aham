package com.pivot.aham.api.service.mapper.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@Accessors(chain = true)
public class SaxoBalETFHoldMoneyPO implements Serializable {
    private Long id;
    private String productCode;
    private BigDecimal saxoHoldShare;
    private BigDecimal dasHoldShare;
    private BigDecimal saxoHoldAmount;
    private BigDecimal dasHoldAmount;
    private String statusDes;
    private String fileName;
    private String transNumber;
    private String compareTime;

    public SaxoBalETFHoldMoneyPO() {
    }

    public SaxoBalETFHoldMoneyPO(String compareTime) {
        this.compareTime = compareTime;
    }
}
