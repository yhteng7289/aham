package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountBalanceHisRecordMapper;
import com.pivot.aham.api.service.mapper.model.AccountBalanceHisRecord;
import com.pivot.aham.api.service.service.AccountBalanceHisRecordService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author addison
 */
@Service
public class AccountBalanceHisRecordServiceImpl extends BaseServiceImpl<AccountBalanceHisRecord, AccountBalanceHisRecordMapper> implements AccountBalanceHisRecordService {

    @Override
    public void updateByAccountId(AccountBalanceHisRecord accountBalanceHisRecord) {
        mapper.updateByAccountId(accountBalanceHisRecord);
    }
}
