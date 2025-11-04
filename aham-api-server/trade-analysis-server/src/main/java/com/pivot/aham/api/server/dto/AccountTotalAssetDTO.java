package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class AccountTotalAssetDTO extends BaseDTO {
    //总DAS现金资产
    private BigDecimal totalCash;
    //etf总持有金额
    private BigDecimal totalHold;

    public AccountTotalAssetDTO() {
    }

    public AccountTotalAssetDTO(BigDecimal totalCash, BigDecimal totalHold) {
        this.totalCash = totalCash;
        this.totalHold = totalHold;
    }
}
