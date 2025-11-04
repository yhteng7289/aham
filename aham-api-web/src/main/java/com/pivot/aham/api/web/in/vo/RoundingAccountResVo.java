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
@ApiModel("RoundingHandlingAccountResVo-回值对象说明")
public class RoundingAccountResVo {

    @ApiModelProperty(value = "Transaction amount", required = true)
    private BigDecimal operateMoney;
    @ApiModelProperty(value = "Time to create", required = true)
    private Date createTime;
    @ApiModelProperty(value = "Operate Type", required = true)
    private OperateTypeEnum operateType;
    @ApiModelProperty(value = "Account ID", required = true)
    private Long accountId;
    @ApiModelProperty(value = "Client ID", required = true)
    private Long clientId;
    @ApiModelProperty(value = "Goal ID", required = true)
    private String goalId;
    @ApiModelProperty(value = "Transaction No", required = true)
    private String redeemId;
}
