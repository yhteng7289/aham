package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月01日
 */
@Data
@Accessors(chain=true)
public class UserSetGoalMoneyDTO extends BaseVo {
    private String goalId;
    private String clientId;
    private BigDecimal money = BigDecimal.ZERO;
    private CurrencyEnum currencyType;

}
