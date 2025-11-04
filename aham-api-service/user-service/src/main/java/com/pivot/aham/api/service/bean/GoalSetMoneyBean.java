package com.pivot.aham.api.service.bean;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 19/1/11.
 */
@Data
@Accessors(chain = true)
public class GoalSetMoneyBean extends BaseDTO{
    private BigDecimal money = BigDecimal.ZERO;
    private String clientId;
    private String goalId;
    private String portfolioId;
//    private RiskLevelEnum riskLevel;
//    private AgeLevelEnum ageLevel;
    private String referenceCode;
    private String virtualAccountNo;
    private CurrencyEnum currency;
    private String sgdVirtualAccountNo;

}
