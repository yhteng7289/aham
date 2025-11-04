package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PivotPftAccountResDTO extends BaseDTO {
    /**
     * 产品code
     */
    private String productCode;
    /**
     * 份额
     */
    private BigDecimal share;
    /**
     * 金额
     */
    private BigDecimal money;
}
