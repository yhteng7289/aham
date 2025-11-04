package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

@Data
public class PivotPftProductReqDTO extends BaseDTO {
    /**
     * 产品code
     */
    private String productCode;

}
