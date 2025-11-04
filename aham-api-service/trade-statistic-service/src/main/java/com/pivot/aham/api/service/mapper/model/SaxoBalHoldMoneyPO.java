package com.pivot.aham.api.service.mapper.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class SaxoBalHoldMoneyPO implements Serializable {
    private Long id;
    private BigDecimal saxoHoldMoney;
    private BigDecimal dasHoldMoney;
    private String statusDes;
    private String fileName;
    private String transNumber;
    private String compareTime;

    public SaxoBalHoldMoneyPO(BigDecimal saxoHoldMoney, BigDecimal dasHoldMoney, String statusDes, String fileName, String transNumber, String compareTime) {
        this.saxoHoldMoney = saxoHoldMoney;
        this.dasHoldMoney = dasHoldMoney;
        this.statusDes = statusDes;
        this.fileName = fileName;
        this.transNumber = transNumber;
        this.compareTime = compareTime;
    }

    public SaxoBalHoldMoneyPO(String compareTime) {
        this.compareTime = compareTime;
    }

    public SaxoBalHoldMoneyPO() {
    }
}
