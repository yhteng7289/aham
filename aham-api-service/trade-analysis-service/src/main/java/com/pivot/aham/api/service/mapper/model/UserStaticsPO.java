package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户过程数据统计
 * 统计时机：最后账户统计完成后
 *
 * @author addison
 * @since 2019年02月22日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_statics",resultMap = "UserStaticsRes")
public class UserStaticsPO extends BaseModel{
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


    private Date startStaticDate;
    private Date endStaticDate;
    private List<String> goalIdList;

}
