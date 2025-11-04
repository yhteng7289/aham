package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 19/2/21.
 */
@Data
@Accessors(chain = true)
public class ClassfiyEtfVo {
    @ApiModelProperty(value = "用户资产中持有一级分类的名称", required = true)
    private String classfiyName;
    @ApiModelProperty(value = "用户资产中持有的etf的百分比", required = true)
    private BigDecimal percentage;
    @ApiModelProperty(value = "每支classfiy下的etf百分比", required = true)
    private List<EtfPercentageVo> etfPercentageVoList;

}
