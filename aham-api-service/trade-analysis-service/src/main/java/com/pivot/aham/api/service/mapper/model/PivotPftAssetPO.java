package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.analysis.PftAssetSourceEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName(value = "t_pivot_pft_asset",resultMap = "PivotPftAssetRes")
public class PivotPftAssetPO extends BaseModel {
    /**
     * 产品code
     */
    private String productCode;
    /**
     * 确认份额
     */
    private BigDecimal confirmShare;
    /**
     * 确认金额
     */
    private BigDecimal confirmMoney;
    /**
     * 出入金类型
     */
    private ProductAssetStatusEnum productAssetStatus;
    /**
     * 执行订单id
     */
    private Long executeOrderNo;
    /**
     * 执行时间
     */
    private Date executeTime;
    /**
     * 操作来源
     */
    private PftAssetSourceEnum pftAssetSource;
}
