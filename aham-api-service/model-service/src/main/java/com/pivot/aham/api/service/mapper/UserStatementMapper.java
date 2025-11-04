package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UserStatementPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
public interface UserStatementMapper extends BaseMapper<UserStatementPO> {

    UserStatementPO selectByStaticDate(UserStatementPO userCustStatementPO);

    List<UserStatementPO> queryByClientId(UserStatementPO userCustStatementPO);
}
