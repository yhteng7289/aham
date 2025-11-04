package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.enums.CurrencyEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
@ApiModel("ClientGoalReqVo-请求对象说明")
public class ClientGoalResBeanVo {
    @ApiModelProperty(value = "clientId",required = true)
    private String clientId;
    @ApiModelProperty(value = "goalId",required = true)
    private String goalId;
    @ApiModelProperty(value = "referenceCode",required = true)
    private String referenceCode;
    @ApiModelProperty(value = "portfolioId",required = true)
    private String portfolioId;
    @ApiModelProperty(value = "币种",required = true)
    private CurrencyEnum currency;
    @ApiModelProperty(value = "goal的资产",required = true)
    private BigDecimal assetValue;
    @ApiModelProperty(value = "goal的sgd资产",required = true)
    private BigDecimal assetValueSgd;
    @ApiModelProperty(value = "goal入金",required = true)
    private BigDecimal totalDeposit;
    @ApiModelProperty(value = "goal提现",required = true)
    private BigDecimal totalWithdrawal;
    @ApiModelProperty(value = "goal收益",required = true)
    private BigDecimal totalReturn;
    @ApiModelProperty(value = "goal策略收益",required = true)
    private BigDecimal portfolioReturn;
    @ApiModelProperty(value = "汇率收益",required = true)
    private BigDecimal fxImpact;
    @ApiModelProperty(value = "最新的T2汇率",required = true)
    private BigDecimal exchangeRate;
    @ApiModelProperty(value = "创建时间",required = true)
    private Date createTime;
    private Long accountId;
}
