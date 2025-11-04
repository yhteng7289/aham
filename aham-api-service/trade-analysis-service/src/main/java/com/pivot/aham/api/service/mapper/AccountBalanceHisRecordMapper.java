package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountBalanceHisRecord;
import com.pivot.aham.common.core.base.BaseMapper;

public interface AccountBalanceHisRecordMapper extends BaseMapper<AccountBalanceHisRecord> {
    void updateByAccountId(AccountBalanceHisRecord accountBalanceHisRecord);

}