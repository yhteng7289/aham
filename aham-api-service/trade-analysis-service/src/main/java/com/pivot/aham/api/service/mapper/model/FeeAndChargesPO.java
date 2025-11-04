package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class FeeAndChargesPO extends BaseModel {
    private String goalId;
    private String goalName;
    private BigDecimal monthlyAvgAsset;
    private BigDecimal mgtFee;
    private BigDecimal custFee;
    private BigDecimal gstMgtFee;
    private BigDecimal perFee;
    private BigDecimal gstPerFee;

    private BigDecimal monthlyAvgAssetSgd;
    private BigDecimal mgtFeeSgd;
    private BigDecimal custFeeSgd;
    private BigDecimal gstMgtFeeSgd;
    private BigDecimal perFeeSgd;
    private BigDecimal gstPerFeeSgd;
}
