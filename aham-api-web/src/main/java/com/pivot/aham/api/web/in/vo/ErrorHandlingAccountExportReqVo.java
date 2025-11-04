package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("ErrorHandlingAccountExportReqVo-请求对象说明")
public class ErrorHandlingAccountExportReqVo {

    @ApiModelProperty(value = "交易开始日期", required = false)
    private Date tradeStartDate;
    @ApiModelProperty(value = "交易结束日期", required = false)
    private Date tradeEndDate;

}
