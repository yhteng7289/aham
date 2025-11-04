package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("DividendReqVo")
public class DividendReqVo {
    @ApiModelProperty(value = "accountId",required = true)
    private Long accountId;
    @ApiModelProperty(value = "产品code",required = false)
    private String productCode;
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "goalId",required = false)
    private String goalId;
    @ApiModelProperty(value = "clientId",required = false)
    private String clientId;
}
