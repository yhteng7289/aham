package com.pivot.aham.api.web.app.dto.reqdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DepositReqDTO extends BaseDTO {
    private String clientId;
    private String goalId;
    private String portfolioId;
}
