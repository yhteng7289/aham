package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.ModelStatusEnum;
import com.pivot.aham.common.enums.PoolingEnum;
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
public class ModelRecommendResDTO extends BaseDTO {

    private Date modelTime;
    private String productWeight;
    private String classfiyWeight;
    private PoolingEnum pool;
    private RiskLevelEnum risk;
    private AgeLevelEnum age;
    private String portfolioId;
    private ModelStatusEnum modelStatus;
    private BigDecimal score;
    //If 10 days maxdrawdown of VOO >0.05
    private Boolean vooTenDays;

}
