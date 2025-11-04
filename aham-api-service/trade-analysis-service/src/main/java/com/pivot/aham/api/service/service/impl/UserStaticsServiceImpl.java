package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserStaticsMapper;
import com.pivot.aham.api.service.mapper.model.UserStaticsPO;
import com.pivot.aham.api.service.service.UserStaticsService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserStaticsServiceImpl extends BaseServiceImpl<UserStaticsPO, UserStaticsMapper> implements UserStaticsService {
    @Override
    public List<UserStaticsPO> queryListByTime(UserStaticsPO userStaticsPO) {
        return mapper.queryListByTime(userStaticsPO);
    }

    @Override
    public UserStaticsPO selectByStaticDate(UserStaticsPO userStaticsPO) {
        return mapper.selectByStaticDate(userStaticsPO);
    }

    @Override
    public List<UserStaticsPO> queryUserStatics(UserStaticsPO userStaticsPO) {
        return mapper.queryUserStatics(userStaticsPO);
    }
}
