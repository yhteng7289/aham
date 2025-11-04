package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountStaticsPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface AccountStaticsMapper extends BaseMapper<AccountStaticsPO> {

    AccountStaticsPO selectByStaticDate(AccountStaticsPO accountStaticsPO);

    List<AccountStaticsPO> selectListByStaticDate(AccountStaticsPO accountStaticsPO);

    AccountStaticsPO selectLastStatic(AccountStaticsPO accountStaticsPO);

}
