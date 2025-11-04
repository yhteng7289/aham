package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountPerformanceFeeDetails;
import com.pivot.aham.common.core.base.BaseService;
import java.math.BigDecimal;
import java.util.List;


public interface AccountPerformanceFeeDetailsService extends BaseService<AccountPerformanceFeeDetails> {
    
    void saveAccountPerformanceFeeDetails(AccountPerformanceFeeDetails accountPerformanceFeeDetails);
    
    AccountPerformanceFeeDetails getLastAccPerFeeDetails(AccountPerformanceFeeDetails accountPerformanceFeeDetails);
    
    List<AccountPerformanceFeeDetails> getListAccPerformanceFeeDetails(AccountPerformanceFeeDetails accountPerformanceFeeDetails);
    
    BigDecimal getSumDeductedShare(AccountPerformanceFeeDetails accountPerformanceFeeDetails);
     
}
