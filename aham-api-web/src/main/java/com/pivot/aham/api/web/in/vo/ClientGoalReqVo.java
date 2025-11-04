package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("ClientGoalReqVo-请求对象说明")
public class ClientGoalReqVo {
    @ApiModelProperty(value = "clientId",required = false)
    private String clientId;
    @ApiModelProperty(value = "goalId",required = false)
    private String goalId;



}
