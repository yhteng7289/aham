package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.core.util.HandleDot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("ClientGoalEtfResVo-请求对象说明")
public class ClientGoalEtfResVo {
    @ApiModelProperty(value = "clientId",required = true)
    private String clientId;
    @ApiModelProperty(value = "goalId",required = true)
    private String goalId;
    @ApiModelProperty(value = "产品code",required = true)
    private String productCode;
    @ApiModelProperty(value = "份额",required = true)
    private BigDecimal share;
    @ApiModelProperty(value = "金额",required = true)
    private BigDecimal amount;
    @ApiModelProperty(value = "etf分类",required = true)
    private String classifyName;
    @ApiModelProperty(value = "实际占比",required = true)
    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal percentage;
}
