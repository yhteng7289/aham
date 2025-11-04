package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface AccountUserMapper extends BaseMapper<AccountUserPO> {

    AccountUserPO queryAccountUser(AccountUserPO queryParam);

    void insertAccountUser(AccountUserPO accountUserPO);

    List<AccountUserPO> listByAccountUserPo(AccountUserPO queryParam);

    List<AccountUserPO> listAccountUserBeforeEffectTime(AccountUserPO accountUserPO);

}
