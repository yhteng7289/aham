package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.PftHoldingStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PivotPftHoldingResDTO extends BaseDTO {

    private Long merdeOrderId;

    private Long etfOrderId;

    private BigDecimal share;
    
    private PftHoldingStatusEnum status;
    
    private String productCode;
}
