package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountFundNavPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface AccountFundNavMapper extends BaseMapper<AccountFundNavPO> {

    AccountFundNavPO selectOneByNavTime(AccountFundNavPO accountFundNavPO);

    void insertAccountFundNav(AccountFundNavPO todayFundNav);

    void updateAccountFundNav(AccountFundNavPO todayFundNav);

    List<AccountFundNavPO> queryListByNavTime(AccountFundNavPO accountFundNavPO);

}