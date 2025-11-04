package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("HomeStaticsResVo-请求对象说明")
public class HomeStaticsResVo {
    @ApiModelProperty(value = "当前系统注册用户数",required = true)
    private int numOfUsers;
    @ApiModelProperty(value = "系统所有用户所有goal上的总资产",required = true)
    private String totalInvestmentSgd;
    @ApiModelProperty(value = "系统所有用户所有goal的总入金",required = true)
    private String totalDepositSgd;
    @ApiModelProperty(value = "系统所有用户所有goal的总提现",required = true)
    private String totalWithdrawalSgd;
    @ApiModelProperty(value = "系统中所有用户所有goal的总收益",required = true)
    private String totalReturn;
    @ApiModelProperty(value = "系统中所有用户所有goal的总策略收益",required = true)
    private String portfolioReturn;
    @ApiModelProperty(value = "系统中所有用户所有goal的总汇率收益",required = true)
    private String fxImpact;
    @ApiModelProperty(value = "系统中所有用户的新币虚拟账号资产加和",required = true)
    private String totalSquirrelCashSgd;
    @ApiModelProperty(value = "系统中所有用户的美金虚拟账号资产加和",required = true)
    private String totalSquirrelCashUsd;
    @ApiModelProperty(value = "更新时间",required = true)
    private Date updateTime;


}
