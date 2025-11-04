package com.pivot.aham.api.service.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.AccountInfoMapper;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.api.service.service.AccountInfoService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = "accountInfo")
@Service
public class AccountInfoServiceImpl
        extends BaseServiceImpl<AccountInfoPO, AccountInfoMapper> implements AccountInfoService {

    @Override
    public AccountInfoPO queryAccountInfo(AccountInfoPO accountInfoPO) {
        return mapper.queryAccountInfo(accountInfoPO);
    }

    @Override
    public void insert(AccountInfoPO accountInfoTailor) {
        mapper.insertAccount(accountInfoTailor);
    }

    @Override
    public List<AccountInfoPO> listAccountInfo() {
        return mapper.listAccountInfo();
    }

    @Override
    public List<AccountInfoPO> listAccountInfos(AccountInfoPO po) {
        return mapper.listAccountInfos(po);
    }

    @Override
    public Page<AccountInfoPO> listPageAccountInfo(AccountInfoPO po, Page<AccountInfoPO> rowBounds) {
        List<AccountInfoPO> accountInfoPOList = mapper.listPageAccountInfo(po,rowBounds);
        rowBounds.setRecords(accountInfoPOList);
        return rowBounds;
    }
}
