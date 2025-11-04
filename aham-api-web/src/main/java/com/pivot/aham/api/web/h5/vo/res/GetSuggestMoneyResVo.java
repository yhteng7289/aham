package com.pivot.aham.api.web.h5.vo.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class GetSuggestMoneyResVo {

    @ApiModelProperty(value = "投资期限", required = true)
    private String frequency;
    @ApiModelProperty(value = "投资类型", required = true)
    private String goalsType;
    @ApiModelProperty(value = "portfolioId", required = true)
    private String portfolioId;
    @ApiModelProperty(value = "投资金额", required = true)
    private BigDecimal recommendAmt;
    @ApiModelProperty(value = "child姓名", required = false)
    private String childName;
    @ApiModelProperty(value = "clientId", required = true)
    private String clientId;
    @ApiModelProperty(value = "risk", required = true)
    private String risk;
}
