package com.pivot.aham.api.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;


public interface AccountInfoService extends BaseService<AccountInfoPO> {

    AccountInfoPO queryAccountInfo(AccountInfoPO accountInfoPO);

    void insert(AccountInfoPO accountInfoTailor);

    List<AccountInfoPO> listAccountInfo();

    List<AccountInfoPO> listAccountInfos(AccountInfoPO po);

    Page<AccountInfoPO> listPageAccountInfo(AccountInfoPO po, Page<AccountInfoPO> rowBounds);
}
