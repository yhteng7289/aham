package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("AccountEtfReqVo-请求对象说明")
public class AccountEtfReqVo {
    @ApiModelProperty(value = "accountId",required = true)
    private Long accountId;
//    @ApiModelProperty(value = "页码", required = true)
//    private Integer pageNo;
//    @ApiModelProperty(value = "页大小", required = true)
//    private Integer pageSize;
}
