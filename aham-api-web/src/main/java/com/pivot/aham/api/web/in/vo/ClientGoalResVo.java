package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("ClientGoalResVo-请求对象说明")
public class ClientGoalResVo {
    @ApiModelProperty(value = "用户在goal上的资产与虚拟账号的资产总和SGD",required = true)
    private BigDecimal totalWealthSgd;
//    @ApiModelProperty(value = "用户在goal上的资产与虚拟账号的资产总和USD",required = true)
//    private BigDecimal totalWealthUsd;


    @ApiModelProperty(value = "clientGoalResBeanVoList",required = false)
    List<ClientGoalResBeanVo> clientGoalResBeanVoList;

}
