package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class AssetHoldingPO extends BaseModel {
    private String goalId;
    private String goalName;
    private String productCode;
    private BigDecimal openValue;

    private BigDecimal openPrecnet;
    private BigDecimal dividendRecive;
    private BigDecimal closeValue;

}
