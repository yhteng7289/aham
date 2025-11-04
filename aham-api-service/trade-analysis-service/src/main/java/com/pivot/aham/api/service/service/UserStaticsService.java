package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserStaticsPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface UserStaticsService extends BaseService<UserStaticsPO> {
    List<UserStaticsPO> queryListByTime(UserStaticsPO userStaticsPO);
    UserStaticsPO selectByStaticDate(UserStaticsPO userStaticsPO);
    List<UserStaticsPO> queryUserStatics(UserStaticsPO userStaticsPO);

}
