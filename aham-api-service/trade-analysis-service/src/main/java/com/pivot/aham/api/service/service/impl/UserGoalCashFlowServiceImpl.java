package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserGoalCashFlowMapper;
import com.pivot.aham.api.service.mapper.model.UserGoalCashFlowPO;
import com.pivot.aham.api.service.service.UserGoalCashFlowService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserGoalCashFlowServiceImpl extends BaseServiceImpl<UserGoalCashFlowPO, UserGoalCashFlowMapper> implements UserGoalCashFlowService {
    @Override
    public List<UserGoalCashFlowPO> queryListByTime(UserGoalCashFlowPO userGoalCashFlowPO) {
        return mapper.queryListByTime(userGoalCashFlowPO);
    }

    @Override
    public UserGoalCashFlowPO selectByStaticDate(UserGoalCashFlowPO userGoalCashFlowPO) {
        return mapper.selectByStaticDate(userGoalCashFlowPO);
    }

}
