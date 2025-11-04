package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class UserStaticsResDTO extends BaseDTO {
    private Long accountId;
    private String clientId;
    private String goalId;
    /**
     * 统计日
     */
    private Date staticDate;
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
     * 美元净值
     */
    private BigDecimal navInUsd;
    /**
     * 剩余资产-新币
     */
    private BigDecimal adjFundAssetInSgd;
    /**
     * saxo入金汇率t1
     */
    private BigDecimal fxRateForFundIn;
    /**
     * saxo出金汇率t2
     */
    private BigDecimal fxRateForFundOut;
}
