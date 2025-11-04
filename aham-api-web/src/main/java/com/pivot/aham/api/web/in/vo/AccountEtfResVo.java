package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("AccountEtfResVo-请求对象说明")
public class AccountEtfResVo {
    @ApiModelProperty(value = "productCode",required = true)
    private String productCode;
    @ApiModelProperty(value = "份额",required = true)
    private BigDecimal share;
    @ApiModelProperty(value = "金额",required = true)
    private BigDecimal amount;
}
