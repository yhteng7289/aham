package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountPerformanceFeeMapper;
import com.pivot.aham.api.service.mapper.model.AccountPerformanceFee;
import com.pivot.aham.api.service.service.AccountPerformanceFeeService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AccountPerformanceFeeServiceImpl extends BaseServiceImpl<AccountPerformanceFee, AccountPerformanceFeeMapper> implements AccountPerformanceFeeService {

    @Override
    public void saveAccountPerformanceFee(AccountPerformanceFee accountPerformanceFee) {
        mapper.saveAccountPerformanceFee(accountPerformanceFee);
    }
    
    @Override
    public BigDecimal getSumAccPerformanceFee(AccountPerformanceFee accountPerformanceFee) { 
        return mapper.getSumAccPerformanceFee(accountPerformanceFee);
    }
    
    @Override
    public BigDecimal getSumAccPerformanceFeeGst(AccountPerformanceFee accountPerformanceFee) { 
        return mapper.getSumAccPerformanceFeeGst(accountPerformanceFee);
    }
    
    @Override
    public void updateAccPerformanceFeeStatus(AccountPerformanceFee accountPerformanceFee) {
        mapper.updateAccPerformanceFeeStatus(accountPerformanceFee);
    }
    
    @Override
    public List<AccountPerformanceFee> listAccPerformanceFee(AccountPerformanceFee accountPerformanceFee) { 
        return mapper.listAccPerformanceFee(accountPerformanceFee);
    }
}
