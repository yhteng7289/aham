package com.pivot.aham.api.service.job.custstatment.impl;

import com.pivot.aham.api.service.mapper.model.AssetHoldingPO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AssetHoldingReportBean {
    private String clientId;
    private Long accountId;
    private String goalId;
    private String goalName;
    List<AssetHoldingPO> assetHoldingPOList;

    private BigDecimal totalOpenValue;
    private BigDecimal totalOpenPrecnet;
    private BigDecimal totalDividendRecive;
    private BigDecimal totalCloseValue;

}
