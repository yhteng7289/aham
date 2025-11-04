package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.RiskLevelEnum;
import lombok.Data;

import java.util.Date;

@Data
public class AccountUserResDTO extends BaseDTO {
    private String clientId;
    private Long accountId;
    private String referenceCode;
    private String goalId;
    private String portfolioId;
    private RiskLevelEnum riskLevel;
    private AgeLevelEnum ageLevel;
    private CurrencyEnum firstRechargeCurrency;
    private Date effectTime;
}
