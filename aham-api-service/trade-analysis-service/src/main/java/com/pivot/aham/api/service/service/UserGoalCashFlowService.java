package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserGoalCashFlowPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface UserGoalCashFlowService extends BaseService<UserGoalCashFlowPO> {
    List<UserGoalCashFlowPO> queryListByTime(UserGoalCashFlowPO userGoalCashFlowPO);
    UserGoalCashFlowPO selectByStaticDate(UserGoalCashFlowPO userGoalCashFlowPO);
}
