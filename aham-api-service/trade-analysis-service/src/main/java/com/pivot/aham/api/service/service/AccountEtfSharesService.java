package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

public interface AccountEtfSharesService extends BaseService<AccountEtfSharesPO> {

    List<AccountEtfSharesPO> selectByStaticDate(AccountEtfSharesPO accountEtfSharesPO);

    AccountEtfSharesPO selectByStaticDateByAccountId(AccountEtfSharesPO accountEtfSharesPO);

}
