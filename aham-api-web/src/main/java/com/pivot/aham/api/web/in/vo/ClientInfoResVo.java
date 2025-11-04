package com.pivot.aham.api.web.in.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("ClientInfoResVo-请求对象说明")
public class ClientInfoResVo {
    @ApiModelProperty(value = "clientId",required = true)
    private String clientId;
    @ApiModelProperty(value = "clientName",required = true)
    private String clientName;
    @ApiModelProperty(value = "client下的goal数量",required = true)
    private Integer numOfGoals;
    @ApiModelProperty(value = "新币虚拟账户",required = true)
    private String bankVirtualAccountNoSgd;
    @ApiModelProperty(value = "美元虚拟账户",required = true)
    private String bankVirtualAccountNoUsd;
    @ApiModelProperty(value = "新币总资产",required = true)
    private BigDecimal totalWealthSgd;
    @ApiModelProperty(value = "美元总资产",required = true)
    private BigDecimal totalWealthUsd;
    @ApiModelProperty(value = "新币总投资",required = true)
    private BigDecimal totalInvestmentSgd;
    @ApiModelProperty(value = "美元总投资",required = true)
    private BigDecimal totalInvestmentUsd;
    @ApiModelProperty(value = "虚拟账户新币总额",required = true)
    private BigDecimal totalSquirrelCashSgd;
    @ApiModelProperty(value = "虚拟账户美元总额",required = true)
    private BigDecimal totalSquirrelCashUsd;
    @ApiModelProperty(value = "用户的注册时间",required = true)
    private Date registrationTime;
}
