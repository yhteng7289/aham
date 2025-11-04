package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 19/3/5.
 */
@Data
@Accessors(chain = true)
public class UserProfitInfoResDTO extends BaseDTO {
    private String clientId;
    private Long accountId;
    private String goalId;
    private BigDecimal totalProfit;
    private BigDecimal portfolioProfit;
    private BigDecimal fxImpact;
    private Date date;

}
