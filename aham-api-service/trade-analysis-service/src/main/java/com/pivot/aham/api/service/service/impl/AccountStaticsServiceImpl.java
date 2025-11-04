package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountStaticsMapper;
import com.pivot.aham.api.service.mapper.model.AccountStaticsPO;
import com.pivot.aham.api.service.service.AccountStaticsService;
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
public class AccountStaticsServiceImpl extends BaseServiceImpl<AccountStaticsPO, AccountStaticsMapper> implements AccountStaticsService {

    @Override
    public AccountStaticsPO selectByStaticDate(AccountStaticsPO accountStaticsPO) {
        return mapper.selectByStaticDate(accountStaticsPO);
    }

    @Override
    public List<AccountStaticsPO> selectListByStaticDate(AccountStaticsPO accountStaticsPO) {
        return mapper.selectListByStaticDate(accountStaticsPO);
    }

    @Override
    public AccountStaticsPO selectLastStatic(AccountStaticsPO accountStaticsPO) {
        return mapper.selectLastStatic(accountStaticsPO);
    }

}
