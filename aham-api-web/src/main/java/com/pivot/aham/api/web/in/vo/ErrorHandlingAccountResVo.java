/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.enums.analysis.ErrorFeeTypeEnum;
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
@ApiModel("ErrorHandlingAccountResVo-回值对象说明")
public class ErrorHandlingAccountResVo {

    @ApiModelProperty(value = "金额", required = true)
    private BigDecimal money;
    @ApiModelProperty(value = "交易时间", required = true)
    private Date createTime;
    @ApiModelProperty(value = "手续费", required = true)
    private OperateTypeEnum operateType;
    @ApiModelProperty(value = "手续费", required = true)
    private ErrorFeeTypeEnum type;

}
