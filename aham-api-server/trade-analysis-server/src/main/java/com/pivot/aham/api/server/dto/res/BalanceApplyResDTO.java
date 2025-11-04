package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceApplyResDTO extends BaseDTO {

    private BigDecimal pivotFee;
    private BigDecimal errorHandlingFee;
    private BigDecimal charityFee;
    private BigDecimal pftCash;

}
