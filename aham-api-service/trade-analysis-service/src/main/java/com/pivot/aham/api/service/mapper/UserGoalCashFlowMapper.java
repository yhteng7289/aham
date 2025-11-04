package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UserGoalCashFlowPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
public interface UserGoalCashFlowMapper extends BaseMapper<UserGoalCashFlowPO> {
    List<UserGoalCashFlowPO> queryListByTime(UserGoalCashFlowPO userGoalCashFlowPO);
    UserGoalCashFlowPO selectByStaticDate(UserGoalCashFlowPO userGoalCashFlowPO);
}
