package com.pivot.aham.api.service.mapper.model;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class PivotErrorHandlingDetailVo {

    private BigDecimal money;
    private String type;
    private String operateType;
    private String operateDate;
}
