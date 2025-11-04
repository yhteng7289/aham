package com.pivot.aham.api.service.job.interevent;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
@Data
public class CalFundNavEvent {
    private Long accountId;
    /**
     * 总etf价值
     */
    private BigDecimal totalEquity;
    /**
     * 卖出前的现金总额
     */
    private BigDecimal cashHolding;
    /**
     *Total Equity Value + Cash Holding
     */
    private BigDecimal totalFundValue;
    /**
     * 自建基金份额
     */
    private BigDecimal fundShares;
    /**
     * 美元净值
     */
    private BigDecimal navInUsd;
    /**
     * 账户提现总金额
     */
    private BigDecimal cashWithdraw;
    /**
     * 剩余份额
     */
    private BigDecimal adjFundShares;
    /**
     * 剩余资产
     */
    private BigDecimal adjFundAsset;
    /**
     * 剩余现金
     */
    private BigDecimal adjCashHolding;
    
    private Date date; //Added By WooiTatt

}
