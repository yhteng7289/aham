package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountEtfSharesStaticPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.Date;
import java.util.List;


public interface AccountEtfSharesStaticService extends BaseService<AccountEtfSharesStaticPO> {
    AccountEtfSharesStaticPO selectByStaticDate(AccountEtfSharesStaticPO accountEtfSharesPO);
    AccountEtfSharesStaticPO getListByDate( Date nowDate);
    List<AccountEtfSharesStaticPO> selectListByStaticDate(AccountEtfSharesStaticPO accountEtfSharesPO);
}
