package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class AssetValueVo {
    @ApiModelProperty(value = "日期", required = true)
    private String date;
    @ApiModelProperty(value = "assetValue", required = true)
    private BigDecimal assetValue;
    @ApiModelProperty(value = "netDeposit", required = true)
    private BigDecimal netDeposit;

}
