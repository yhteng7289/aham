package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.PerformanceFeeStatusEnum;
import com.pivot.aham.common.enums.analysis.PerformanceFeeTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author WooiTatt
 * @since 2019年01月22日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_performance_fee",resultMap = "AccountPerformanceFeeRes")
public class AccountPerformanceFee extends BaseModel{
    
    private String accountId;
    private String clientId;
    private String goalId;
    private BigDecimal performanceFee;
    private BigDecimal performanceFeeGst;
    private BigDecimal performanceTotalFee;
    private PerformanceFeeStatusEnum status;
    private PerformanceFeeTypeEnum feeType;
    private Date createTime;
    private Date updateTime;
    
    private Date startTime;
    private Date endTime;
}
