package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ModelRecommendForAppResVo返回对象说明")
public class GetCountriesResVo {
    List<String> countries;
}
