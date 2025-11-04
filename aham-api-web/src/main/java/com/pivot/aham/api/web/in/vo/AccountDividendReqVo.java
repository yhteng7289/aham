package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("AccountDividendReqVo-请求对象说明")
public class AccountDividendReqVo {
    @ApiModelProperty(value = "accountId",required = true)
    private Long accountId;
    @ApiModelProperty(value = "交易开始日期",required = false)
    private Date tradeStartDate;
    @ApiModelProperty(value = "交易结束日期",required = false)
    private Date tradeEndDate;
    @ApiModelProperty(value = "产品code",required = false)
    private String productCode;
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
}
