package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UserFundNavPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface UserFundNavMapper extends BaseMapper<UserFundNavPO> {

    UserFundNavPO selectOneByNavTime(UserFundNavPO userFundNavPO);

    void updateUserFundNav(UserFundNavPO todayUserFundNav);

    void insertUserFundNav(UserFundNavPO todayUserFundNav);

    List<UserFundNavPO> queryListByTime(UserFundNavPO userFundNavPO);

    UserFundNavPO selectLastOne(UserFundNavPO userFundNavPO);

    List<UserFundNavPO> listUserFundNav(UserFundNavPO userFundNavPO);

}