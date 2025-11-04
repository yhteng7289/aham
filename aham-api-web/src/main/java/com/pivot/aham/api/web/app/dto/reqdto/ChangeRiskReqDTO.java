package com.pivot.aham.api.web.app.dto.reqdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class ChangeRiskReqDTO extends BaseDTO{
    private String clientId;
    private String portfolioId;
}
