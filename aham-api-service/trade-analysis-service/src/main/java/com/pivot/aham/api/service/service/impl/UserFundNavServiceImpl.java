package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserFundNavMapper;
import com.pivot.aham.api.service.mapper.model.UserFundNavPO;
import com.pivot.aham.api.service.service.UserFundNavService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Service
public class UserFundNavServiceImpl extends BaseServiceImpl<UserFundNavPO, UserFundNavMapper> implements UserFundNavService {

    @Override
    public UserFundNavPO selectOneByNavTime(UserFundNavPO userFundNavPO) {
        return mapper.selectOneByNavTime(userFundNavPO);
    }

    @Override
    public void saveTodayUserFundNav(UserFundNavPO todayUserFundNav) {
        UserFundNavPO queryPo = new UserFundNavPO();
        queryPo.setAccountId(todayUserFundNav.getAccountId());
        queryPo.setClientId(todayUserFundNav.getClientId());
        queryPo.setNavTime(todayUserFundNav.getNavTime());
        queryPo.setGoalId(todayUserFundNav.getGoalId());
        UserFundNavPO alreadyExistPo = mapper.selectOneByNavTime(queryPo);
        if (null != alreadyExistPo) {
            todayUserFundNav.setId(alreadyExistPo.getId());
            mapper.updateUserFundNav(todayUserFundNav);
        } else {
            mapper.insertUserFundNav(todayUserFundNav);
        }
    }

    @Override
    public List<UserFundNavPO> queryListByTime(UserFundNavPO userFundNavPO) {
        return mapper.queryListByTime(userFundNavPO);
    }

    @Override
    public UserFundNavPO selectUserGoalLastOne(Long accountId, String clientId, String goalId) {
        UserFundNavPO userFundNavPO = new UserFundNavPO();
        userFundNavPO.setAccountId(accountId);
        userFundNavPO.setClientId(clientId);
        userFundNavPO.setGoalId(goalId);
        return mapper.selectLastOne(userFundNavPO);
    }

    @Override
    public List<UserFundNavPO> listUserFundNav(UserFundNavPO userFundNavPO) {
        return mapper.listUserFundNav(userFundNavPO);
    }

}
