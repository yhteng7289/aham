package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.ModelStatusEnum;
import com.pivot.aham.common.enums.RiskLevelEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
public class ModelRecommendBackUp extends BaseModel {
    private Date modelTime;
    private String productWeight;
    private RiskLevelEnum risk;
    private AgeLevelEnum age;
    private String classfiyWeight;
    private String portfolioId;
    private ModelStatusEnum modelStatus;
    private BigDecimal score;
    private BigDecimal currency;

}
