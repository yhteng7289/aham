package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("SaxoAccountOrderResVo-请求对象说明")
public class SaxoAccountOrderResVo {
    @ApiModelProperty(value = "accountId",required = true)
    private Long accountId;
    @ApiModelProperty(value = "交易单号",required = true)
    private String transNo;
    @ApiModelProperty(value = "产品code",required = true)
    private String productCode;
    @ApiModelProperty(value = "份额",required = true)
    private BigDecimal shares;
    @ApiModelProperty(value = "金额",required = true)
    private BigDecimal amount;
    @ApiModelProperty(value = "手续费",required = true)
    private BigDecimal commission;
    @ApiModelProperty(value = "交易类型",required = true)
    private EtfOrderTypeEnum saxoOrderTransType;
    @ApiModelProperty(value = "交易时间",required = true)
    private Date transTime;

}
