package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountEtfSharesMapper;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.api.service.service.AccountEtfSharesService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountEtfSharesServiceImpl extends BaseServiceImpl<AccountEtfSharesPO, AccountEtfSharesMapper> implements AccountEtfSharesService {

    @Override
    public List<AccountEtfSharesPO> selectByStaticDate(AccountEtfSharesPO accountEtfSharesPO) {
        return mapper.selectByStaticDate(accountEtfSharesPO);
    }

    @Override
    public AccountEtfSharesPO selectByStaticDateByAccountId(AccountEtfSharesPO accountEtfSharesPO) {
        return mapper.selectByStaticDateByAccountId(accountEtfSharesPO);
    }
}
