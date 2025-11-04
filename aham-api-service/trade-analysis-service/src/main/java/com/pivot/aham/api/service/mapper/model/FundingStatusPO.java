package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import com.pivot.aham.common.enums.recharge.UserRechargeStatusEnum;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author bjoon
 */
@Data
@Accessors(chain = true)
public class FundingStatusPO extends BaseModel  {
    private String clientId;
    private String goalId;
    private BigDecimal applyAmountInSgd;
    private String confirmAmountInSgd;
    private BigDecimal applyAmountInUsd;
    private OperateTypeEnum operateTypeEnum;
    private String status;
    private String status2;
    private Date createTime;
    
    private Date startCreateTime;
    private Date endCreateTime;
}
