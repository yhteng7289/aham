package com.pivot.aham.api.web.in.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("ClientInfoReqVo-请求对象说明")
public class ClientInfoReqVo {
    @ApiModelProperty(value = "clientId",required = false)
    private String clientId;
    @ApiModelProperty(value = "clientName",required = false)
    private String clientName;
//    @ApiModelProperty(value = "虚拟账户",required = false)
//    private String bankVirtualAccountNo;
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;


}
