package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.in.TransStatusEnum;
import com.pivot.aham.common.enums.in.TransTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransOrderReqDTO extends BaseDTO {
    private String clientId;
    private String goalId;
    private String referenceCode;
    private String portfolioId;
    private String transNo;
    private Date transTime;
    private TransTypeEnum transType;
    private BigDecimal amountUsd;
    private BigDecimal amountSgd;
    private TransStatusEnum transStatus;

    private Integer pageNo;
    private Integer pageSize;


    private Date startTranscationTime;
    private Date endTranscationTime;
}
