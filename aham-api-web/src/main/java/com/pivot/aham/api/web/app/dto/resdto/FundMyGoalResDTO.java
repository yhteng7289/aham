package com.pivot.aham.api.web.app.dto.resdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class FundMyGoalResDTO extends BaseDTO{
    private String goalName;

    private String orderId;

    private String goalId;

    private BigDecimal applyMoney;

    private String clientId;

    private String date;

    private String time;

    private String resultCode;
    private String errorMsg;
}
