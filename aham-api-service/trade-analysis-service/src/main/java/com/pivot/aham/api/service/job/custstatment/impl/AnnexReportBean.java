package com.pivot.aham.api.service.job.custstatment.impl;

import com.pivot.aham.api.service.mapper.model.AnnexPO;
import lombok.Data;

import java.util.List;

@Data
public class AnnexReportBean {
    private String clientId;
    private Long accountId;
    private String goalId;
    private String goalName;
    List<AnnexPO> annexPOList;

}
