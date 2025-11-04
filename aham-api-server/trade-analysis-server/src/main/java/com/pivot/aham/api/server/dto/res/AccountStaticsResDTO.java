package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountStaticsResDTO extends BaseDTO {
    private Long accountId;
    /**
     * 统计日
     */
    private Date staticDate;
    /**
     * 总etf价值
     */
    private BigDecimal totalEquity;
    /**
     * etf购买剩余金额
     */
    private BigDecimal cashResidual;
    /**
     * etf卖出金额
     */
    private BigDecimal cashBySell;
    /**
     * 卖出前的现金总额
     */
    private BigDecimal cashHolding;
    /**
     * 现金分红
     */
    private BigDecimal cashDividend;
    /**
     * 管理费
     */
    private BigDecimal mgtFee;
    private BigDecimal custFee;
    private BigDecimal gstMgtFee;
    private BigDecimal perFee;
    private BigDecimal gstPerFee;

    /**
     * unbuy金额
     */
    private BigDecimal unbuyAmount;
    /**
     * 超额现金
     */
    private BigDecimal excessCash;
    /**
     * 购买时的手续费
     */
    private BigDecimal transactionCostBuy;
    /**
     * 卖出时产生的手续费
     */
    private BigDecimal transactionCostSell;
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
    /**
     * 新币净值
     */
    private BigDecimal navInSgd;
    /**
     * 剩余资产-新币
     */
    private BigDecimal adjFundAssetInSgd;
    /**
     * 剩余新币-新币
     */
    private BigDecimal cashWithdrawInSgd;
    /**
     * saxo入金汇率 T1
     */
    private BigDecimal fxRateForFundIn;
    /**
     * saxo出金汇率 T2
     */
    private BigDecimal fxRateForFundOut;
    /**
     * 每天收盘时汇率
     */
    private BigDecimal fxRateForClearing;
}
