package com.pivot.aham.api.web.web.vo.res;

import com.pivot.aham.common.enums.RiskLevelEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "风险等级介绍")
public class RiskLevelRemarkResVo {

    private RiskLevelEnum riskLevel;
    private String remark;
    private Integer value;
}
