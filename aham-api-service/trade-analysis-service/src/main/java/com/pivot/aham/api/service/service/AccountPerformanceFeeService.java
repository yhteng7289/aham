package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountPerformanceFee;
import com.pivot.aham.common.core.base.BaseService;
import java.math.BigDecimal;
import java.util.List;


public interface AccountPerformanceFeeService extends BaseService<AccountPerformanceFee> {
    
    void saveAccountPerformanceFee(AccountPerformanceFee accountPerformanceFee);
    
    BigDecimal getSumAccPerformanceFee(AccountPerformanceFee accountPerformanceFee);
    
    BigDecimal getSumAccPerformanceFeeGst(AccountPerformanceFee accountPerformanceFee);
    
    void updateAccPerformanceFeeStatus(AccountPerformanceFee accountPerformanceFee);
    
    List <AccountPerformanceFee> listAccPerformanceFee(AccountPerformanceFee accountPerformanceFee);
    
}
