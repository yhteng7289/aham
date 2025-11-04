package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountNormalFeeMapper;
import com.pivot.aham.api.service.mapper.model.AccountNormalFee;
import com.pivot.aham.api.service.service.AccountNormalFeeService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AccountNormalFeeServiceImpl extends BaseServiceImpl<AccountNormalFee, AccountNormalFeeMapper> implements AccountNormalFeeService {

    @Override
    public AccountNormalFee selectByDay(AccountNormalFee accountNormalFee) {
        return mapper.selectByDay(accountNormalFee);
    }
}
