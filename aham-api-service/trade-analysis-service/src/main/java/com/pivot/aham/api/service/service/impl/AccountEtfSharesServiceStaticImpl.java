package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountEtfSharesStaticMapper;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesStaticPO;
import com.pivot.aham.api.service.service.AccountEtfSharesStaticService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AccountEtfSharesServiceStaticImpl extends BaseServiceImpl<AccountEtfSharesStaticPO, AccountEtfSharesStaticMapper> implements AccountEtfSharesStaticService {

    @Override
    public AccountEtfSharesStaticPO selectByStaticDate(AccountEtfSharesStaticPO accountEtfSharesPO) {
        return mapper.selectByStaticDate(accountEtfSharesPO);
    }
    @Override
    public AccountEtfSharesStaticPO getListByDate(Date nowDate) {
        return mapper.getListByDate(nowDate);
    }

    @Override
    public List<AccountEtfSharesStaticPO> selectListByStaticDate(AccountEtfSharesStaticPO accountEtfSharesPO) {
        return mapper.selectListByStaticDate(accountEtfSharesPO);
    }
}
