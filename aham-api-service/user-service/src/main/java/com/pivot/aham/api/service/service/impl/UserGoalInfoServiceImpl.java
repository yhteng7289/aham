package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.service.mapper.UserGoalInfoMapper;
import com.pivot.aham.api.service.mapper.model.UserGoalInfoPO;
import com.pivot.aham.api.service.service.UserGoalInfoInfoService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by luyang.li on 18/12/2.
 */
@Service
public class UserGoalInfoServiceImpl extends BaseServiceImpl<UserGoalInfoPO, UserGoalInfoMapper> implements UserGoalInfoInfoService {

    @Override
    public UserGoalInfoPO getUserGoal(UserGoalInfoPO userGoalInfoPO) {
        return mapper.queryUserGoalInfo(userGoalInfoPO);
    }

    @Override
    public int saveUserGoalInfo(UserGoalInfoDTO userGoalInfoDTO) {
        UserGoalInfoPO userGoalInfo = dtoConvertToModel(userGoalInfoDTO);
        return mapper.saveUserGoalInfo(userGoalInfo);
    }

    @Override
    public void saveUserGoalInfos(List<UserGoalInfoDTO> userGoalInfoDTOs) {
        List<UserGoalInfoPO> userGoalInfos = userGoalInfoDTOs.stream().map(item -> {
            UserGoalInfoPO userGoalInfo = new UserGoalInfoPO();
            userGoalInfo.setClientId(item.getClientId())
                    .setReferenceCode(StringUtils.trim(item.getReferenceCode()))
                    .setGoalId(item.getGoalId())
                    .setPortfolioId(item.getPortfolioId())
                    .setGoalName(item.getGoalName())
                    .setCreateTime(DateUtils.now())
                    .setUpdateTime(DateUtils.now())
                    .setId(Sequence.next());
            return userGoalInfo;
        }).collect(Collectors.toList());
        mapper.saveUserGoalInfos(userGoalInfos);
    }

    @Override
    public List<UserGoalInfoPO> queryUserGoalInfos(UserGoalInfoPO userGoalInfoPO) {
        return mapper.queryUserGoalInfos(userGoalInfoPO);
    }

    @Override
    public UserGoalInfoPO queryUserGoalInfo(UserGoalInfoPO userGoalInfoPO) {
        return mapper.queryUserGoalInfo(userGoalInfoPO);
    }
    
    @Override
    public UserGoalInfoPO queryUserGoalInfoForStatement(UserGoalInfoPO userGoalInfoPO) {
        return mapper.queryUserGoalInfoForStatement(userGoalInfoPO);
    }

    private UserGoalInfoPO dtoConvertToModel(UserGoalInfoDTO dto) {
        UserGoalInfoPO userGoalInfo = new UserGoalInfoPO();
        userGoalInfo.setClientId(dto.getClientId())
                .setPortfolioId(dto.getPortfolioId())
                .setGoalId(dto.getGoalId())
                .setReferenceCode(dto.getReferenceCode())
                .setUpdateTime(DateUtils.now())
                .setCreateTime(DateUtils.now())
                .setId(Sequence.next());
        return userGoalInfo;
    }

    private UserGoalInfoDTO modelConvertToDto(UserGoalInfoPO userGoalInfo) {
        UserGoalInfoDTO dto = new UserGoalInfoDTO();
        dto.setClientId(userGoalInfo.getClientId())
                .setReferenceCode(userGoalInfo.getReferenceCode())
                .setGoalId(userGoalInfo.getGoalId())
                .setPortfolioId(userGoalInfo.getPortfolioId())
                .setCreateTime(userGoalInfo.getCreateTime());
        dto.setUpdateTime(userGoalInfo.getUpdateTime());
        return dto;
    }

    @Override
    public void updateDeletedByClientIdAndGoalId(String clientId, String goalId) {
        mapper.updateDeletedByClientIdAndGoalId(clientId, goalId);
    }

}
