package com.pivot.aham.api.service.job.custstatment.impl;

import com.pivot.aham.api.service.mapper.model.FeeAndChargesPO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FeeAndChargesBean {
    private String clientId;
    private List<FeeAndChargesPO> feeAndChargesList;
    private BigDecimal totalMgtFee;
    private BigDecimal totalCustFee;
    private BigDecimal subTotal;
    private BigDecimal totalGst;
    private BigDecimal total;




}
