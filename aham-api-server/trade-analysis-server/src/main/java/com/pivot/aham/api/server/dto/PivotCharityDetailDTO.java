package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PivotCharityDetailDTO extends BaseDTO {

    private OperateTypeEnum operateType;
    private BigDecimal operateMoney;
    private Date operateTime;
    private Long redeemId;
    private String transNo;

}
