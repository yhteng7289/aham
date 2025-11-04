package com.pivot.aham.api.service.mapper.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DailyTwapPricePO {
    private Long id;
    private String etfCode;
    private String bsnDt;
    private BigDecimal avgAsk;
    private BigDecimal avgBid;
    private Integer avgCount;
    private Date createTime;
}
