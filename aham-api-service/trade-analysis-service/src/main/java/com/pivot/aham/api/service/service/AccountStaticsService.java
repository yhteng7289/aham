package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountStaticsPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface AccountStaticsService extends BaseService<AccountStaticsPO> {

    AccountStaticsPO selectByStaticDate(AccountStaticsPO accountStaticsPO);

    List<AccountStaticsPO> selectListByStaticDate(AccountStaticsPO accountStaticsPO);

    AccountStaticsPO selectLastStatic(AccountStaticsPO accountStaticsPO);

}
