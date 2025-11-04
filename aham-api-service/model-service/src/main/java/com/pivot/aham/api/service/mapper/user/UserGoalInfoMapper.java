package com.pivot.aham.api.service.mapper.user;

import com.pivot.aham.api.service.mapper.model.user.UserGoalInfo;
import com.pivot.aham.common.core.base.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author luyang.li
 * @date 18/12/4
 */
@Repository(value = "goalInfoMapper")
public interface UserGoalInfoMapper extends BaseMapper<UserGoalInfo> {

    int saveUserGoalInfo(UserGoalInfo userGoalInfo);

    void saveUserGoalInfos(List<UserGoalInfo> userGoalInfos);

    List<UserGoalInfo> queryUserGoalInfos(UserGoalInfo userGoalInfoPO);

    UserGoalInfo queryUserGoalInfo(UserGoalInfo userGoalInfoPO);
    
    void updateDeletedByGoalId(@Param("goalId") String goalId);

}
