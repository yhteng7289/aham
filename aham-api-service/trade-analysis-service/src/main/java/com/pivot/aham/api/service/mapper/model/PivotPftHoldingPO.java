package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.PftHoldingStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "t_pivot_pft_holding",resultMap = "PivotPftHoldingPO")
public class PivotPftHoldingPO extends BaseModel {

    private Long merdeOrderId;

    private Long etfOrderId;

    private BigDecimal share;
    
    private PftHoldingStatusEnum status;
    
    private String productCode;


}