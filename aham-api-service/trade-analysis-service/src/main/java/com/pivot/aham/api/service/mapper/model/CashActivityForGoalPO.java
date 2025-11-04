package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class CashActivityForGoalPO extends BaseModel {
    private Long custStatementId;
    private String goalId;
    private String activityDesc;
    private BigDecimal activityAmountSgd;
    private BigDecimal activityAmountUsd;
    private Date activityTime;

}
