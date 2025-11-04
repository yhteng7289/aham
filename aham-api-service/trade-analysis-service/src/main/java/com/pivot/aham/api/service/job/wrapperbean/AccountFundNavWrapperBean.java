package com.pivot.aham.api.service.job.wrapperbean;

import com.pivot.aham.api.service.mapper.model.AccountFundNavPO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年02月25日
 */
@Data
public class AccountFundNavWrapperBean {
    private AccountFundNavPO accountFundNavPO;
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


}
