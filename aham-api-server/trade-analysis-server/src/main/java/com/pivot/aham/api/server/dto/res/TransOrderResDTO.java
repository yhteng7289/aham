package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.in.TransStatusEnum;
import com.pivot.aham.common.enums.in.TransTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransOrderResDTO extends BaseDTO {
    private String clientId;
    private String transNo;
    private String goalId;
    private Date transcationTime;
    private TransTypeEnum transType;
    private BigDecimal amountUsd;
    private TransStatusEnum transStatus;
}
