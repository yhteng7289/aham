package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月29日
 */
@Data
@Accessors(chain = true)
public class UserGoalInfoResDTO extends BaseDTO{
    private String clientId;
    private String goalId;
    private String goalName;
    private String portfolioId;
    private String referenceCode;

}
