package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.AccountBalanceRecord;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface AccountBalanceRecordMapper extends BaseMapper<AccountBalanceRecord> {
    List<AccountBalanceRecord> queryAccountBalance(AccountBalanceRecord accountBalanceRecord);
    List<AccountBalanceRecord> getAccountBalRecordPage(Page<AccountBalanceRecord> rowBounds, AccountBalanceRecord accountBalanceRecord);

}