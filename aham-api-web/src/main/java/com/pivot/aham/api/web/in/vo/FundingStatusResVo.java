package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import com.pivot.aham.common.enums.recharge.UserRechargeStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author bjoon
 */
@Data
@ApiModel("FundingStatusResVo")
public class FundingStatusResVo {
    @ApiModelProperty(value = "clientId",required = true)
    private String clientId;
    @ApiModelProperty(value = "goalId",required = true)
    private String goalId;
    @ApiModelProperty(value = "Transaction amount(SGD)", required = true)
    private BigDecimal applyAmountInSgd;
    @ApiModelProperty(value = "Confirm Transaction amount(SGD)", required = true)
    private String confirmAmountInSgd;
    @ApiModelProperty(value = "Transaction amount(USD)", required = true)
    private BigDecimal applyAmountInUsd;
    @ApiModelProperty(value = "Operate Type", required = true)
    private OperateTypeEnum operateType;
    @ApiModelProperty(value = "Status", required = true)
    private String status;
    @ApiModelProperty(value = "Status2", required = true)
    private String status2;
    @ApiModelProperty(value = "Time to create", required = true)
    private Date createTime;
    @ApiModelProperty(value = "Conversion Amt(SGD)", required = true)
    private BigDecimal confirmInSgd;
}
