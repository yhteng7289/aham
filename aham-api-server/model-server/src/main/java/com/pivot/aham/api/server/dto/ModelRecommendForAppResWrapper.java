package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by luyang.li on 19/3/5.
 */
@Data
@Accessors(chain = true)
public class ModelRecommendForAppResWrapper extends BaseDTO {

    private String portfolioId;
    @ApiModelProperty(value = "模型数据:modelData", required = true)
    private List<ClassfiyEtfWrapper> modelData;
    //最大回撤
    private String maxDD;
    private String sharpRadio;
    private String aveReturn;

}
