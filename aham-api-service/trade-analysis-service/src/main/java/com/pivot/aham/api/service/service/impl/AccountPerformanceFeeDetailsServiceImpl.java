package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountPerformanceFeeDetailsMapper;
import com.pivot.aham.api.service.mapper.model.AccountPerformanceFeeDetails;
import com.pivot.aham.api.service.service.AccountPerformanceFeeDetailsService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AccountPerformanceFeeDetailsServiceImpl extends BaseServiceImpl<AccountPerformanceFeeDetails, AccountPerformanceFeeDetailsMapper> implements AccountPerformanceFeeDetailsService {

    @Override
    public void saveAccountPerformanceFeeDetails(AccountPerformanceFeeDetails accountPerformanceFeeDetails) {
        mapper.saveAccountPerformanceFeeDetails(accountPerformanceFeeDetails);
    }
    
    @Override
    public AccountPerformanceFeeDetails getLastAccPerFeeDetails(AccountPerformanceFeeDetails accountPerformanceFeeDetails) {
        return mapper.getLastAccPerFeeDetails(accountPerformanceFeeDetails);
    }
    
    @Override
    public List<AccountPerformanceFeeDetails> getListAccPerformanceFeeDetails(AccountPerformanceFeeDetails accountPerformanceFeeDetails) {
        return mapper.getListAccPerformanceFeeDetails(accountPerformanceFeeDetails);
    }
    
    @Override
    public BigDecimal getSumDeductedShare(AccountPerformanceFeeDetails accountPerformanceFeeDetails) {
        return mapper.getSumDeductedShare(accountPerformanceFeeDetails);
    }
}
