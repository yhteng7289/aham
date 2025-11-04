package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface AccountEtfSharesMapper extends BaseMapper<AccountEtfSharesPO> {

    List<AccountEtfSharesPO> selectByStaticDate(AccountEtfSharesPO accountEtfSharesPO);

    AccountEtfSharesPO selectByStaticDateByAccountId(AccountEtfSharesPO accountEtfSharesPO);
}
