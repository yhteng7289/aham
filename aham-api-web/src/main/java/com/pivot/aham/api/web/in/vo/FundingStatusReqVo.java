package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author bjoon
 */
@Data
@ApiModel("FundingStatusReqVo")
public class FundingStatusReqVo {
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "clientId",required = false)
    private String clientId;
    @ApiModelProperty(value = "开始时间", required = true)
    private Date startCreateTime;
    @ApiModelProperty(value = "结束时间", required = true)
    private Date endCreateTime;
    @ApiModelProperty(value = "operationtype", required = false)
    private String operationType;
}
