package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.ModelStatusEnum;
import com.pivot.aham.common.enums.RiskLevelEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
public class ModelRecommendDTO extends BaseDTO {
    private Date modelTime;
    private String productWeight;
    private String classfiyWeight;
    private RiskLevelEnum risk;
    private AgeLevelEnum age;
    private String portfolioId;
    private ModelStatusEnum modelStatus;
    private Integer score;
}
