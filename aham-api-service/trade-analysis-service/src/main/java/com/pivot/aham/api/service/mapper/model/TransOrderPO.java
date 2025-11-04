package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.in.TransStatusEnum;
import com.pivot.aham.common.enums.in.TransTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransOrderPO extends BaseModel {
    private String clientId;
    private String transNo;
    private String goalId;
    private Date transcationTime;
    private TransTypeEnum transType;
    private BigDecimal amountUsd;
    private TransStatusEnum transStatus;

    private Date startTranscationTime;
    private Date endTranscationTime;

}
