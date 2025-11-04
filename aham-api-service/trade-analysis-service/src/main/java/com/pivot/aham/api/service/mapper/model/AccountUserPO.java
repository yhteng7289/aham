package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.RiskLevelEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 账户用户关系
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_user",resultMap = "accountUserRes")
public class AccountUserPO extends BaseModel {
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
