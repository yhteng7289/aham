package com.pivot.aham.api.web.in.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.pivot.aham.common.core.util.HandleDot;
import com.pivot.aham.common.enums.AccountTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("AccountInfoResVo-请求对象说明")
public class AccountInfoResVo {
    @ApiModelProperty(value = "accountId",required = true)
    private Long accountId;
    @ApiModelProperty(value = "基金净值",required = true)
    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal fundNav;
    @ApiModelProperty(value = "总份额",required = true)
    private BigDecimal totalShares;
    @ApiModelProperty(value = "总资产(USD)",required = true)
    private BigDecimal totalAsset;
    @ApiModelProperty(value = "总资产(SGD)",required = true)
    private BigDecimal totalAssetSGD;
    @ApiModelProperty(value = "总金额",required = true)
    private BigDecimal totalCash;
    @ApiModelProperty(value = "管理费",required = true)
    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal mgtFee;
    @ApiModelProperty(value = "附加费",required = true)
    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal custFee;
    @ApiModelProperty(value = "管理附加费",required = true)
    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal mgtGst;
    @ApiModelProperty(value = "分红",required = true)
    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal dividend;
    @ApiModelProperty(value = "账户类型",required = true)
    private AccountTypeEnum accountType;
    @ApiModelProperty(value = "用户个数",required = true)
    private Integer numOfClients;
    @ApiModelProperty(value = "数据统计时间",required = true)
    @JSONField(format="yyyy-MM-dd")
    private Date staticDate;
    private String goalId;

}
