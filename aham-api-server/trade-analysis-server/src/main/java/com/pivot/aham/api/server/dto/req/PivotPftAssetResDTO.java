package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PivotPftAssetResDTO extends BaseDTO {

    private String productCode;

    private BigDecimal confirmShare;

    private BigDecimal confirmMoney;

}
