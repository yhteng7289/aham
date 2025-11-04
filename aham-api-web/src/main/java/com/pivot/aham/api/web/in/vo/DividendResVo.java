package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("DividendResVo")
public class DividendResVo {
    @ApiModelProperty(value = "clientId",required = true)
    private String clientId;
    @ApiModelProperty(value = "goalId",required = false)
    private String goalId;
    @ApiModelProperty(value = "goalName",required = false)
    private String goalName;
    @ApiModelProperty(value = "portfolioId",required = false)
    private String portfolioId;
    @ApiModelProperty(value = "referenceCode",required = false)
    private String referenceCode;
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
    
    private Long accountId;
    private Date dividendDate;
    private BigDecimal dividendAmount;
    private String productCode;
    
}
