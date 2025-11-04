package com.pivot.aham.api.service.mapper.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 每日汇率
 */
@Data
public class DailyExchangeRatePO {
    private Long id;
    private String bsnDt;
    private BigDecimal usdToSgd;
    private Date createTime;
}
