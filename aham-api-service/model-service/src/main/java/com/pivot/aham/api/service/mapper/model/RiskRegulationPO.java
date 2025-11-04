package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RiskRegulationPO extends BaseModel {
    private Integer totalScore;
    private Integer ageLevel;
    private Integer riskLevel;
    private Integer poolLevel;
}
