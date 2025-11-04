package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UserGoalInfoPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by luyang.li on 18/12/4.
 */
public interface UserGoalInfoMapper extends BaseMapper<UserGoalInfoPO> {

    int saveUserGoalInfo(UserGoalInfoPO userGoalInfo);

    void saveUserGoalInfos(List<UserGoalInfoPO> userGoalInfos);

    List<UserGoalInfoPO> queryUserGoalInfos(UserGoalInfoPO userGoalInfoPO);

    UserGoalInfoPO queryUserGoalInfo(UserGoalInfoPO userGoalInfoPO);
    
    UserGoalInfoPO queryUserGoalInfoForStatement(UserGoalInfoPO userGoalInfoPO);//Added by WooiTatt

    void updateDeletedByClientIdAndGoalId(@Param("clientId") String clientId, @Param("goalId") String goalId);

}
