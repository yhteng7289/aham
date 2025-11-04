package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountNormalFee;
import com.pivot.aham.common.core.base.BaseService;


public interface AccountNormalFeeService extends BaseService<AccountNormalFee> {
    AccountNormalFee selectByDay(AccountNormalFee accountNormalFee);
}
