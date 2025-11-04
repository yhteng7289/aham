package com.pivot.aham.api.service.service;/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */

import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface AccountUserService extends BaseService<AccountUserPO>{

    AccountUserPO queryAccountUser(AccountUserPO queryParam);

    void insertAccountUser(AccountUserPO accountUserPO);

    List<AccountUserPO> listByAccountUserPo(AccountUserPO queryParam);

    /**
     * 查询effectTime前注册的用户
     *
     * @param accountUserParam
     * @return
     */
    List<AccountUserPO> listAccountUserBeforeEffectTime(AccountUserPO accountUserParam);

}
