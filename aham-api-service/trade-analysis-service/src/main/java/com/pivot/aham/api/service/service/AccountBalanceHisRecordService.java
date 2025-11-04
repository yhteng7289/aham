package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountBalanceHisRecord;
import com.pivot.aham.common.core.base.BaseService;


public interface AccountBalanceHisRecordService extends BaseService<AccountBalanceHisRecord> {
     void updateByAccountId(AccountBalanceHisRecord accountBalanceHisRecord);
}
