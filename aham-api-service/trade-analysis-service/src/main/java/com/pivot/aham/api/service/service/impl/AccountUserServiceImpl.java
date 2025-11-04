package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountUserMapper;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.service.AccountUserService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Service
public class AccountUserServiceImpl extends BaseServiceImpl<AccountUserPO, AccountUserMapper> implements AccountUserService {

    @Override
    public AccountUserPO queryAccountUser(AccountUserPO queryParam) {
        return mapper.queryAccountUser(queryParam);
    }

    @Override
    public void insertAccountUser(AccountUserPO accountUserPO) {
        mapper.insertAccountUser(accountUserPO);
    }

    @Override
    public List<AccountUserPO> listByAccountUserPo(AccountUserPO queryParam) {
        return mapper.listByAccountUserPo(queryParam);
    }
    @Override
    public List<AccountUserPO> listAccountUserBeforeEffectTime(AccountUserPO accountUserPO) {
        return mapper.listAccountUserBeforeEffectTime(accountUserPO);
    }

}
