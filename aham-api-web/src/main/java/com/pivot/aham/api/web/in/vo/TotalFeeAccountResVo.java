/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author HP
 */
@Data
@ApiModel("TotalAccountManagementFeeResVo-回值对象说明")
public class TotalFeeAccountResVo {

    @ApiModelProperty(value = "金额", required = true)
    private BigDecimal money;
    @ApiModelProperty(value = "accountId", required = true)
    private Long accountId;
    @ApiModelProperty(value = "clientId", required = true)
    private Long clientId;
    @ApiModelProperty(value = "goalId", required = true)
    private String goalId;
    @ApiModelProperty(value = "交易时间", required = true)
    private Date createTime;
    @ApiModelProperty(value = "行动时间", required = true)
    private Date operateDate;
    @ApiModelProperty(value = "手续费", required = true)
    private OperateTypeEnum operateType;

}
