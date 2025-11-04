package com.pivot.aham.api.service.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.AccountBalanceRecordMapper;
import com.pivot.aham.api.service.mapper.model.AccountBalanceRecord;
import com.pivot.aham.api.service.service.AccountBalanceRecordService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author addison
 */
@Service
public class AccountBalanceRecordServiceImpl extends BaseServiceImpl<AccountBalanceRecord, AccountBalanceRecordMapper> implements AccountBalanceRecordService {


    @Override
    public List<AccountBalanceRecord> queryAccountBalance(AccountBalanceRecord accountBalanceRecord) {
        return mapper.queryAccountBalance(accountBalanceRecord);
    }
    @Override
    public Page<AccountBalanceRecord> getAccountBalRecordPage(Page<AccountBalanceRecord> rowBounds, AccountBalanceRecord accountBalanceRecord) {
        List<AccountBalanceRecord> accountBalanceRecordList = mapper.getAccountBalRecordPage(rowBounds,accountBalanceRecord);
        rowBounds.setRecords(accountBalanceRecordList);
        return rowBounds;
    }
}
