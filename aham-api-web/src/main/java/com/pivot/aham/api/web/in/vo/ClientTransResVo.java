package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.core.util.HandleDot;
import com.pivot.aham.common.enums.in.TransStatusEnum;
import com.pivot.aham.common.enums.in.TransTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("ClientTransResVo-请求对象说明")
public class ClientTransResVo {
    @ApiModelProperty(value = "clientId",required = true)
    private String clientId;
    @ApiModelProperty(value = "goalId",required = true)
    private String goalId;
    @ApiModelProperty(value = "referenceCode",required = true)
    private String referenceCode;
    @ApiModelProperty(value = "模型id",required = true)
    private String portfolioId;
    @ApiModelProperty(value = "交易单号",required = true)
    private String transNo;
    @ApiModelProperty(value = "交易时间",required = true)
    private Date transTime;
    @ApiModelProperty(value = "交易类型",required = true)
    private TransTypeEnum transType;
    @ApiModelProperty(value = "交易金额USD",required = true)
    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal amountUsd;
    @ApiModelProperty(value = "交易金额SGD",required = true)
    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal amountSgd;
    @ApiModelProperty(value = "交易状态",required = true)
    private TransStatusEnum transStatus;


}
