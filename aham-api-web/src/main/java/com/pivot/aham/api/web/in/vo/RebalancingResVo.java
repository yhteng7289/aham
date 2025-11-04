package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("RebalancingResVo")
public class RebalancingResVo {
    
    @ApiModelProperty(value = "accountId",required = false)
    private Long accountId;
    @ApiModelProperty(value = "lastBalTime",required = false)
    private Date lastBalTime;
    @ApiModelProperty(value = "balId",required = false)
    private Long balId;
    @ApiModelProperty(value = "lastProductWeight",required = false)
    private String lastProductWeight;
    @ApiModelProperty(value = "portfolioScore",required = false)
    private BigDecimal portfolioScore;
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
    
    
}
