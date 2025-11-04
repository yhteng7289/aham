package com.pivot.aham.api.service.job.interevent;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年02月25日
 */
@Data
public class StaticForEtfCallBackEvent {
    private Long accountId;
    /**
     * etf购买剩余金额
     */
    private BigDecimal cashResidual;
    /**
     * etf卖出金额
     */
    private BigDecimal cashBySell;
    /**
     * 购买时的手续费
     */
    private BigDecimal transactionCostBuy;
    /**
     * 卖出时产生的手续费
     */
    private BigDecimal transactionCostSell;




}
