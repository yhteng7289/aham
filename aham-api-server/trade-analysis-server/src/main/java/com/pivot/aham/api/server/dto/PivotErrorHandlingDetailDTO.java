package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.ErrorFeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PivotErrorHandlingDetailDTO extends BaseDTO {

    private BigDecimal money;
    private ErrorFeeTypeEnum type;
    private Date operateDate;
    private OperateTypeEnum operateType;
    private String transNo;

    public PivotErrorHandlingDetailDTO(BigDecimal money, ErrorFeeTypeEnum type, Date operateDate, OperateTypeEnum operateType, String transNo) {
        this.money = money;
        this.type = type;
        this.operateDate = operateDate;
        this.operateType = operateType;
        this.transNo = transNo;
    }

    public PivotErrorHandlingDetailDTO() {
    }
}
