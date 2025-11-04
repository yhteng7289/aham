package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class AccountSummaryPO extends BaseModel {
    private BigDecimal portfolioOpenValue;
    private BigDecimal portfolioCloseValue;
    private BigDecimal squirrelCashOpenValue;
    private BigDecimal squirrelCashCloseValue;
    private BigDecimal totalOpenValue;
    private BigDecimal totalCloseValue;

    private BigDecimal portfolioOpenValueSgd;
    private BigDecimal portfolioCloseValueSgd;
    private BigDecimal squirrelCashOpenValueSgd;
    private BigDecimal squirrelCashCloseValueSgd;
    private BigDecimal totalOpenValueSgd;
    private BigDecimal totalCloseValueSgd;

}
