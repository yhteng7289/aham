package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.RedeemTypeEnum;
import com.pivot.aham.common.enums.analysis.RedeemOrderStatusEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
@Accessors
@TableName(value = "t_account_redeem",resultMap = "AccountRedeemRes")
public class AccountRedeemPO extends BaseModel{
    private Long accountId;
    private String clientId;
    private Date redeemApplyTime;
    private Date redeemConfirmTime;
    private BigDecimal applyMoney;
    private BigDecimal confirmMoney;
    private BigDecimal confirmShares;
    private RedeemOrderStatusEnum orderStatus;
    private Long totalTmpOrderId;
    private String goalId;
    private Long redeemApplyId;
    private TncfStatusEnum tncfStatus;
    private Date navDate;
    private Date tncfTime;
    /**
     * 提现类型
     */
    private RedeemTypeEnum redeemType;
    private BigDecimal oldApplyMoney;

    /**
     * 查询条件
     */
    private Date startRedeemApplyTime;
    private Date endRedeemApplyTime;

    //Added by WooiTatt
    private String isAnnualPerformanceFee; 
    private Long navBatchId;
    private Long accPerformanceFeeDesId;

}
