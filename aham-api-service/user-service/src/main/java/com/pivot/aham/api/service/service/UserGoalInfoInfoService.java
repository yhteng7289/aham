package com.pivot.aham.api.service.service;

import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.service.mapper.model.UserGoalInfoPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

public interface UserGoalInfoInfoService extends BaseService<UserGoalInfoPO> {

    /**
     * 查询用户Goal信息
     *
     * @return
     * @param userGoalInfoParam
     */
    UserGoalInfoPO getUserGoal(UserGoalInfoPO userGoalInfoParam);

    /**
     *
     *
     * @param userGoalInfoDTO
     * @return
     */
    int saveUserGoalInfo(UserGoalInfoDTO userGoalInfoDTO);

    /**
     * 批量保存goal信息
     *
     * @param userGoalInfoDTOs
     */
    void saveUserGoalInfos(List<UserGoalInfoDTO> userGoalInfoDTOs);

    /**
     * 查询用户的goal信息
     *
     * @param userGoalInfoPO
     * @return
     */
    List<UserGoalInfoPO> queryUserGoalInfos(UserGoalInfoPO userGoalInfoPO);

    /**
     * 获取用户goalInfo
     *
     * @param userGoalInfoParam
     * @return
     */
    UserGoalInfoPO queryUserGoalInfo(UserGoalInfoPO userGoalInfoParam);
    
     /**
     * 获取用户goalInfo
     *
     * @param userGoalInfoParam
     * @return
     */
    UserGoalInfoPO queryUserGoalInfoForStatement(UserGoalInfoPO userGoalInfoParam);

    /**
     * 删除 GOAL
     *
     * @param clientId
     * @param goalId
     */
    void updateDeletedByClientIdAndGoalId(String clientId, String goalId);

}
