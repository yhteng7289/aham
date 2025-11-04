package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.RiskLevelEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 19/1/11.
 */
@Data
@Accessors(chain = true)
public class GoalSetMoneyDTO extends BaseDTO{
    private BigDecimal money = BigDecimal.ZERO;
    private String clientId;
    private String goalId;
    private String portfolioId;
    private RiskLevelEnum riskLevel;
    private AgeLevelEnum ageLevel;
    private String referenceCode;
    private String virtualAccountNo;
    private CurrencyEnum currency;
    private String sgdVirtualAccountNo;

}
