package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("AccountBalAdjDetailReqVo-请求对象说明")
public class AccountBalAdjDetailReqVo {
    @ApiModelProperty(value = "调仓记录id",required = true)
    private Long balId;
//    @ApiModelProperty(value = "页码", required = false)
//    private Integer pageNo;
//    @ApiModelProperty(value = "页大小", required = false)
//    private Integer pageSize;
}
