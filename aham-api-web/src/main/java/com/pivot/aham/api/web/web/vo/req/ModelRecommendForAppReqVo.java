package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.ModelRecommendForAppDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class ModelRecommendForAppReqVo {

    @ApiModelProperty(value = "模型数据请求参数", required = true)
    @NotBlank(message = "portfolioId 不能为空")
    private String portfolioId;

    public ModelRecommendForAppDTO convertToDto(ModelRecommendForAppReqVo modelRecommendForAppReqVo) {
        ModelRecommendForAppDTO modelRecommendForAppDTO = new ModelRecommendForAppDTO();
        modelRecommendForAppDTO.setPortfolioId(modelRecommendForAppReqVo.getPortfolioId());
        return modelRecommendForAppDTO;
    }
}
