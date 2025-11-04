package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("ClientGoalEtfReqVo-请求对象说明")
public class ClientGoalEtfReqVo {
    @ApiModelProperty(value = "clientId",required = true)
    private String clientId;
    @ApiModelProperty(value = "goalId",required = true)
    private String goalId;
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
}
