package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserCustStatementPO;
import com.pivot.aham.common.core.base.BaseService;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface UserCustStatementService extends BaseService<UserCustStatementPO> {
    UserCustStatementPO selectByStaticDate(UserCustStatementPO userCustStatementPO);


}
