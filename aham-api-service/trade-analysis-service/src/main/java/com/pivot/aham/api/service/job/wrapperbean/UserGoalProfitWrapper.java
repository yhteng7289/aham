package com.pivot.aham.api.service.job.wrapperbean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 19/3/6.
 */
@Data
@Accessors
public class UserGoalProfitWrapper {

    private BigDecimal totalProfit;
    private BigDecimal portfolioProfit;
    private BigDecimal fxImpact;
}
