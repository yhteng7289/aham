package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("RebalancingReqVo")
public class RebalancingReqVo {

    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;

}
