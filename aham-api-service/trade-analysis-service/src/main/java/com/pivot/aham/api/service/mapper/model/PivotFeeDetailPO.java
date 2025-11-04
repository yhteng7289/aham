package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.FeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName(value = "t_pivot_fee_detail", resultMap = "pivotFeeDetailRes")

public class PivotFeeDetailPO extends BaseModel {

    private BigDecimal money;

    private FeeTypeEnum feeType;

    private OperateTypeEnum operateType;

    private Long accountId;

    private Date createTime;

    private Date operateDate;

    private String goalId;

    private Long clientId;
}
