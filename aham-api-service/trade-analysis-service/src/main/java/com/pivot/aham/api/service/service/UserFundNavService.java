package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserFundNavPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface UserFundNavService extends BaseService<UserFundNavPO> {

    UserFundNavPO selectOneByNavTime(UserFundNavPO userFundNavPO);

    void saveTodayUserFundNav(UserFundNavPO todayUserFundNav);

    List<UserFundNavPO> queryListByTime(UserFundNavPO userFundNavPO);

    UserFundNavPO selectUserGoalLastOne(Long accountId, String clientId, String goalId);

    List<UserFundNavPO> listUserFundNav(UserFundNavPO userFundNavParam);

}
