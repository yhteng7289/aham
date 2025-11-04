package com.pivot.aham.api.web.app.dto.reqdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class GoalReqDTO extends BaseDTO{

    private String clientId;

    private String goalName;

    private String goalId;

    private String applyMoney;

    private String portfolioId;

    private String goalNo;

    private String date;

    private String time;

}
