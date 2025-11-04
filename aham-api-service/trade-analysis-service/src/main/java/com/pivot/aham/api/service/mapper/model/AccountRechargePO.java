package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.RechargeOrderStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
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
@TableName("t_account_recharge")
public class AccountRechargePO extends BaseModel {

    @TableField("account_id")
    private Long accountId;
    @TableField("recharge_time")
    private Date rechargeTime;
    @TableField("currency")
    private CurrencyEnum currency;
    @TableField("recharge_amount")
    private BigDecimal rechargeAmount;
    @TableField("client_id")
    private String clientId;
    @TableField("order_status")
    private RechargeOrderStatusEnum orderStatus;
    @TableField("recharge_order_no")
    private Long rechargeOrderNo;
    @TableField("execute_order_no")
    private Long executeOrderNo;
    @TableField("bank_order_no")
    private String bankOrderNo;
//    private RechargeOperatTypeEnum operatType;
    private String goalId;
    private TpcfStatusEnum tpcfStatus;
    private Date tpcfTime;

    //辅助查询
    private Date rechargeStartTime;
    private Date rechargeEndTime;
    
    private Long totalTmpOrderId;

}
