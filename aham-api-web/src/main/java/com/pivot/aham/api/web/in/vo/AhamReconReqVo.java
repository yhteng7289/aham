package com.pivot.aham.api.web.in.vo;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

    
@Data
@ApiModel("AhamReconReqVo-请求对象说明")
public class AhamReconReqVo {
    @ApiModelProperty(value = "dasUnit", required = false)
    private BigDecimal dasUnit;
    @ApiModelProperty(value = "inputUnit", required = false)
    private BigDecimal inputUnit;
    @ApiModelProperty(value = "dasTime", required = false)
    private Date dasTime;
    @ApiModelProperty(value = "页码", required = false)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = false)
    private Integer pageSize;
    @ApiModelProperty(value = "startCreateTime", required = false)
    private Date startCreateTime;
    @ApiModelProperty(value = "endCreateTime", required = false)
    private Date endCreateTime;
    @ApiModelProperty(value = "prodCode", required = false)
    private String prodCode;
    @ApiModelProperty(value = "orderDetailReqList", required = true)
    List<AhamReconReqVo> orderDetailReqList;
}

