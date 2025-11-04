package com.pivot.aham.api.service.service;

import com.pivot.aham.api.server.dto.UserGoalInfoDTO;

import com.pivot.aham.api.service.mapper.model.user.UserGoalInfo;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * @author YYYz
 */
public interface UserGoalInfoService extends BaseService<UserGoalInfo> {

    /**
     * 查询用户Goal信息
     *
     * @return
     * @param userGoalInfoParam
     */
    UserGoalInfo getUserGoal(UserGoalInfo userGoalInfoParam);

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
    List<UserGoalInfo> queryUserGoalInfos(UserGoalInfo userGoalInfoPO);

    /**
     * 获取用户goalInfo
     *
     * @param userGoalInfoParam
     * @return
     */
    UserGoalInfo queryUserGoalInfo(UserGoalInfo userGoalInfoParam);

    /**
     * 删除 GOAL
     *
     * @param goalId
     */
    void updateDeletedByGoalId(String goalId);

}
