package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
public class UserGoalOrderDTO extends BaseDTO {
    private String clientId;
    private String goalId;
    private String portfolioId;

}
