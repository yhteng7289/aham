package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UserStaticsPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
public interface UserStaticsMapper extends BaseMapper<UserStaticsPO> {
    List<UserStaticsPO> queryListByTime(UserStaticsPO userStaticsPO);
    UserStaticsPO selectByStaticDate(UserStaticsPO userStaticsPO);
    List<UserStaticsPO> queryUserStatics(UserStaticsPO userStaticsPO);

}
