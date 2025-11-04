package com.pivot.aham.api.service.mapper.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 昨日收盘价
 */
@Data
public class DailyClosingPricePO {
    private Long id;
    private String etfCode;
    private Date bsnDt;
    private BigDecimal price;
    private Date createTime;
    private Date navDate;
}
