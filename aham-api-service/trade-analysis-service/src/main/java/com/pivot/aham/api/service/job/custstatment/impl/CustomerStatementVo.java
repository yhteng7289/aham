package com.pivot.aham.api.service.job.custstatment.impl;

import com.pivot.aham.api.service.mapper.model.AccountSummaryPO;
import com.pivot.aham.api.service.mapper.model.GlossaryPO;
import lombok.Data;

import java.util.List;

@Data
public class CustomerStatementVo {
    private String clientId;
    private String clientName;
    private String mobileNum;
    private String address;
//    private Long accountId;
//    private String goalId;
    private String startTime;
    private String endTime;
    private String custStatemnetStaticDate;
    private List<AnnexReportBean> annexReportBeanList;
    private AccountSummaryPO accountSummaryPO;
    private List<GlossaryPO> glossaryPOList;
    private List<AssetHoldingReportBean> assetHoldingReportBeanList;
    private CashActivityBean cashActivityBean;
    private FeeAndChargesBean feeAndChargesBean;
}
