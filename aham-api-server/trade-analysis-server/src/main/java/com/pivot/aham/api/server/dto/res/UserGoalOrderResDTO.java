package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
public class UserGoalOrderResDTO extends BaseDTO {
    private String clientId;
    private Long orderNo;
    private BigDecimal money;
    // 1-investment, 2-withdraw
    private Integer orderType;
    private String orderTime;
    private String goalId;
    // 1-Completed, 2-Processing, 3-Failed
    private Integer orderStatus;

}
