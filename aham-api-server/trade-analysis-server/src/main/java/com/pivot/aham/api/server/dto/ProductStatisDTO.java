package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
@Data
@Accessors(chain = true)
public class ProductStatisDTO extends BaseDTO {
    private String productCode;
    private BigDecimal totalProductMoney;
    private BigDecimal totalProductShare;

    public ProductStatisDTO(String productCode, BigDecimal totalProductMoney, BigDecimal totalProductShare) {
        this.productCode = productCode;
        this.totalProductMoney = totalProductMoney;
        this.totalProductShare = totalProductShare;
    }

    public ProductStatisDTO() {
    }
}
