package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.BalStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BalanceRecordResDTO extends BaseModel {
    private Long accountId;
    private Date balStartTime;
    private Long modelRecommendId;
    private String portfolioId;
    private BigDecimal portfolioScore;
    private Long balTimeDiff;
    private BigDecimal etfDiff;
    private BigDecimal xValue;
    private BalStatusEnum balStatus;
}
