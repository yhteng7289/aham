package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.api.service.client.saxo.resp.OrderActivitiesResp;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class AhamOrderConfirmationPO {
	
    private Long id;
    private String referenceID;
    private String investorID;
    private String scheme;
    private String plan;
    private String tranType;
    private Date tranDate;
    private Date valueDate;
    private String agent;
    private String currency;
    private BigDecimal amount;
    private BigDecimal nav;
    private BigDecimal units;
    private BigDecimal totalCharge;
    private BigDecimal salesChargePercent;
    private BigDecimal salesChargeValue;
    private Date createTime;
    private Date updateTime;
}

