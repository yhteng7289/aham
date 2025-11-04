package com.pivot.aham.api.service.client.saxo.resp;

import com.google.common.collect.Lists;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderConfirmationResp {

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
}
