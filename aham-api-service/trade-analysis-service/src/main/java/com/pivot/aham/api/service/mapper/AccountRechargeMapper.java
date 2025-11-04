package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.common.core.base.BaseMapper;
import java.math.BigDecimal;

import java.util.List;

public interface AccountRechargeMapper extends BaseMapper<AccountRechargePO> {

    void saveAccountRecharge(AccountRechargePO accountRecharge);

    AccountRechargePO queryAccountRecharge(AccountRechargePO accountRechargePO);

    void updateAccountRecharge(AccountRechargePO accountRechargePO);

    List<AccountRechargePO> listByAccountId(AccountRechargePO po);

    List<AccountRechargePO> listAccountRecharge(AccountRechargePO accountRecharge);

    BigDecimal getSumAccountRecharge();
    
    BigDecimal getSumAccRechargeByGoalClient(AccountRechargePO accountRechargePO);
}