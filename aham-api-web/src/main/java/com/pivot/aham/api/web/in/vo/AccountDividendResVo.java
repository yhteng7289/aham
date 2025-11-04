package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.enums.in.DividendTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("AccountDividendResVo-请求对象说明")
public class AccountDividendResVo {
    @ApiModelProperty(value = "accountId",required = true)
    private Long accountId;
    @ApiModelProperty(value = "产品code",required = true)
    private String productCode;
    @ApiModelProperty(value = "交易日期",required = true)
    private Date tradeDate;
    @ApiModelProperty(value = "分红金额",required = true)
    private BigDecimal dividendAmount;
    @ApiModelProperty(value = "分红类型",required = true)
    private DividendTypeEnum dividendType;

}
