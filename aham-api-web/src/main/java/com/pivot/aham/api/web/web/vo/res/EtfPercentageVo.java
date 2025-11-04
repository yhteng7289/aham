package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/10.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户资产goal中的etf详情")
public class EtfPercentageVo {
    @ApiModelProperty(value = "用户资产中持有etf的code", required = true)
    private String etf;
    @ApiModelProperty(value = "用户资产中持有的etf的百分比", required = true)
    private BigDecimal weight;

}
