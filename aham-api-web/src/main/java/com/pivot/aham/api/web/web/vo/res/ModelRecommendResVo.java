package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ModelRecommendReqVo请求对象说明")
public class ModelRecommendResVo {
    @ApiModelProperty(value = "请求模型的日期:date", required = true)
    private String date;
    private String portfolioId;
    private BigDecimal score;
    @ApiModelProperty(value = "模型数据:modelData", required = true)
    private List<ClassfiyEtfVo> modelData;
    //同 portfolio 下的
    private BigDecimal portfolioAveReturn;

}
