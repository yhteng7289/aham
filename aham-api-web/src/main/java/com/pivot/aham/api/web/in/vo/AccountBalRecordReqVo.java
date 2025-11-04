package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("AccountBalRecordReqVo-请求对象说明")
public class AccountBalRecordReqVo {
    @ApiModelProperty(value = "accountId",required = true)
    private Long accountId;
    @ApiModelProperty(value = "调仓开始时间",required = false)
    private Date balStartTime;
    @ApiModelProperty(value = "调仓结束时间",required = false)
    private Date balEndTime;
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
}
