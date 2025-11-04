package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.FeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PivotFeeDetailDTO extends BaseDTO {

    private BigDecimal money;

    private FeeTypeEnum feeType;

    private OperateTypeEnum operateType;

    private Long accountId;

    private Date operateDate;

    private String goalId;

    private Long clientId;
}
