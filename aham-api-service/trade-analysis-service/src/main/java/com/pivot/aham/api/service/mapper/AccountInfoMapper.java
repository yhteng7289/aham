package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface AccountInfoMapper extends BaseMapper<AccountInfoPO> {

    AccountInfoPO getAccountInfo();

    AccountInfoPO queryAccountInfo(AccountInfoPO accountInfoPO);

    void insertAccount(AccountInfoPO accountInfoTailor);

    List<AccountInfoPO> listAccountInfo();

    List<AccountInfoPO> listAccountInfos(AccountInfoPO accountInfoPO);

    List<AccountInfoPO> listPageAccountInfo(AccountInfoPO po, Page<AccountInfoPO> rowBounds);
}
