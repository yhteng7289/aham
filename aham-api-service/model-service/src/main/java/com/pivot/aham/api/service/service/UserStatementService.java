package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserStatementPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface UserStatementService extends BaseService<UserStatementPO> {
    UserStatementPO selectByStaticDate(UserStatementPO userCustStatementPO);


    List<UserStatementPO> queryByClientId(UserStatementPO userCustStatementPO);
}
