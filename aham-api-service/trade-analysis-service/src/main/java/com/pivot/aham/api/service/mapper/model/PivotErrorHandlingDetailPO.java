package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.ErrorFeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName(value = "t_pivot_error_handling_detail", resultMap = "pivotErrorHandlingDetailRes")

public class PivotErrorHandlingDetailPO extends BaseModel {

    private BigDecimal money;
    private ErrorFeeTypeEnum type;
    private OperateTypeEnum operateType;
    private String transNo;
    private Date createTime;
    private Date startDate;
    private Date endDate;
    private Date updateTime;
    private Date operateDate;

}
