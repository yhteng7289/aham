package com.pivot.aham.api.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.AccountBalanceRecord;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;


public interface AccountBalanceRecordService extends BaseService<AccountBalanceRecord> {
    Page<AccountBalanceRecord> getAccountBalRecordPage(
            Page<AccountBalanceRecord> rowBounds, AccountBalanceRecord accountBalanceRecord);
    List<AccountBalanceRecord> queryAccountBalance(AccountBalanceRecord accountBalanceRecord);
}
