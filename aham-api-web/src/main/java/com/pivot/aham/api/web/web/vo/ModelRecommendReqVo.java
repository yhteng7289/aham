package com.pivot.aham.api.web.web.vo;

import com.pivot.aham.api.server.dto.ModelRecommendDTO;
import com.pivot.aham.common.core.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.text.ParseException;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ModelRecommendReqVo请求对象说明")
public class ModelRecommendReqVo {
    @ApiModelProperty(value = "模型数据请求参数:请求模型的日期", required = true)
    @NotBlank(message = "请求日期不能为空")
    private String requiredDate;

    public ModelRecommendDTO convertToDto(ModelRecommendReqVo modelRecommendReqVo) throws ParseException {
        ModelRecommendDTO modelRecommendDTO = new ModelRecommendDTO();
        modelRecommendDTO.setModelTime(DateUtils.parseDate(modelRecommendReqVo.getRequiredDate(), DateUtils.DATE_FORMAT2));
        return modelRecommendDTO;
    }
}
