package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.recharge.UserRechargeStatusEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
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
public class FundingStatusResDTO extends BaseDTO{

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