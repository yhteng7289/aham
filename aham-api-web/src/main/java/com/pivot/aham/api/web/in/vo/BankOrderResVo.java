package com.pivot.aham.api.web.in.vo;


import com.pivot.aham.common.enums.CurrencyEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("BankOrderResVo-请求对象说明")
public class BankOrderResVo {
    @ApiModelProperty(value = "可用金额",required = true)
    private String avaiableAmount;
    @ApiModelProperty(value = "银行单号",required = true)
    private String bankOrderNo;
    @ApiModelProperty(value = "币种",required = true)
    private CurrencyEnum currency;
    @ApiModelProperty(value = "referenceCode",required = true)
    private String referenceCode;
    @ApiModelProperty(value = "收入支出类型",required = true)
    private String typeDesc;
    @ApiModelProperty(value = "匹配类型",required = true)
    private String matchStatusDesc;
    @ApiModelProperty(value = "虚拟账号",required = true)
    private String virtualAccountNo;
    @ApiModelProperty(value = "交易时间",required = true)
    private Date tradeTime;

}

