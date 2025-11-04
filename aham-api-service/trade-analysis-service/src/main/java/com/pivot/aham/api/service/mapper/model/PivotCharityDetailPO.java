package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PivotCharityDetailPO extends BaseModel {

    private OperateTypeEnum operateType;
    private BigDecimal operateMoney;
    private Date operateTime;
    private String goalId;
    private Long id;
    private Long clientId;
    private Long accountId;
    private Long redeemId;
}
