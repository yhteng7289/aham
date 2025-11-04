package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by senyang.zheng on 19/04/18.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ModelRecommendForAppResVo返回对象说明")
public class ModelRecommendForAppResVo {

    private String portfolioId;
    @ApiModelProperty(value = "模型数据:modelData", required = true)
    private List<ClassfiyEtfVo> modelData;
    //最大回撤
    private String maxDD;
    private String sharpRadio;
    private String aveReturn;
    //remark
    private String disclaimer;
}
