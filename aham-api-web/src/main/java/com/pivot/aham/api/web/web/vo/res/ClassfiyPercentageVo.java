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
@ApiModel(value = "用户资产中classfiy的详情")
public class ClassfiyPercentageVo {
    @ApiModelProperty(value = "用户资产中持有一级分类的名称", required = true)
    private String classfiyName;
    @ApiModelProperty(value = "用户资产中持有的etf的百分比", required = true)
    private BigDecimal percentage;

}
