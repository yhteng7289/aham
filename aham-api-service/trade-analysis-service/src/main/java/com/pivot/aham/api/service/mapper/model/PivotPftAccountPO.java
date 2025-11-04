package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "t_pivot_pft_account",resultMap = "PivotPftAccountRes")
public class PivotPftAccountPO extends BaseModel {
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

    /**
     * 数据版本
     */
    private Long dataVersion;
}