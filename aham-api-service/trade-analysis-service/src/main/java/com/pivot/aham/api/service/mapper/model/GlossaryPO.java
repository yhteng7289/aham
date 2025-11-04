package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class GlossaryPO extends BaseModel {
    private BigDecimal portfolioOpenValue;
    private BigDecimal portfolioCloseValue;
    private BigDecimal portfolioOpenValueSgd;
    private BigDecimal portfolioCloseValueSgd;
    private String goalId;
    private String goalName;
    private BigDecimal deposit;
    private BigDecimal depositSgd;
    private BigDecimal withdrawal;
    private BigDecimal withdrawalSgd;

    private BigDecimal totalAb;
    private BigDecimal portfolioA;
    private BigDecimal fxImpact;

    private BigDecimal totalAbSgd;
    private BigDecimal portfolioASgd;
    private BigDecimal fxImpactSgd;

}
