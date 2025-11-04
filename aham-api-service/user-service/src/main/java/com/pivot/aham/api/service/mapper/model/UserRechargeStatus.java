package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.recharge.UserRechargeStatusEnum;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by WooiTatt
 *
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_recharge_status",resultMap = "userRechargeStatusMap")
public class UserRechargeStatus extends BaseModel {
    
    //private Long id;
    
    private String clientId;

    private String goalId;
    
    private CurrencyEnum currency;
    
    private BigDecimal amount;
    
    private UserRechargeStatusEnum userRechargeStatusEnum;
    
    private String bankVirtualAccOrderId;
    
    private String saxoAccOrderId;
    
    private String accRechargeId;
    
    private Date createTime;
    
    private Date updateTime;
    
}
