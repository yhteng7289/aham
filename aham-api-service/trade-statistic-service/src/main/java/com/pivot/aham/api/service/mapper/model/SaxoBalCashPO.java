package com.pivot.aham.api.service.mapper.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class SaxoBalCashPO implements Serializable {
    private Long id;
    private BigDecimal saxoCash;
    private BigDecimal dasCash;
    private BigDecimal diffCash;
    private String statusDes;
    private String fileName;
    private String transNumber;
    private String compareTime;

    public SaxoBalCashPO() {
    }

    public SaxoBalCashPO(BigDecimal saxoCash, BigDecimal dasCash, BigDecimal diffCash, String statusDes, String fileName, String transNumber, String compareTime) {
        this.saxoCash = saxoCash;
        this.dasCash = dasCash;
        this.diffCash = diffCash;
        this.statusDes = statusDes;
        this.fileName = fileName;
        this.transNumber = transNumber;
        this.compareTime = compareTime;
    }

    public SaxoBalCashPO(String compareTime) {
        this.compareTime = compareTime;
    }
}

