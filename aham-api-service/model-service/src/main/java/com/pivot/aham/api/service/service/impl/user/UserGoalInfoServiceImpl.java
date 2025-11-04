package com.pivot.aham.api.service.service.impl.user;

import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.service.mapper.model.user.UserGoalInfo;
import com.pivot.aham.api.service.mapper.user.UserGoalInfoMapper;
import com.pivot.aham.api.service.service.UserGoalInfoService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author luyang.li
 * @date 18/12/2
 */
@Service(value = "userGoalInfoService")
public class UserGoalInfoServiceImpl extends BaseServiceImpl<UserGoalInfo, UserGoalInfoMapper> implements UserGoalInfoService {

    @Override
    public UserGoalInfo getUserGoal(UserGoalInfo userGoalInfoPO) {
        return mapper.queryUserGoalInfo(userGoalInfoPO);
    }

    @Override
    public int saveUserGoalInfo(UserGoalInfoDTO userGoalInfoDTO) {
        UserGoalInfo userGoalInfo = dtoConvertToModel(userGoalInfoDTO);
        return mapper.saveUserGoalInfo(userGoalInfo);
    }

    @Override
    public void saveUserGoalInfos(List<UserGoalInfoDTO> userGoalInfoDTOs) {
        List<UserGoalInfo> userGoalInfos = userGoalInfoDTOs.stream().map(item -> {
            UserGoalInfo userGoalInfo = new UserGoalInfo();
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
    public List<UserGoalInfo> queryUserGoalInfos(UserGoalInfo userGoalInfoPO) {
        return mapper.queryUserGoalInfos(userGoalInfoPO);
    }

    @Override
    public UserGoalInfo queryUserGoalInfo(UserGoalInfo userGoalInfoPO) {
        return mapper.queryUserGoalInfo(userGoalInfoPO);
    }

    private UserGoalInfo dtoConvertToModel(UserGoalInfoDTO dto) {
        UserGoalInfo userGoalInfo = new UserGoalInfo();
        userGoalInfo.setClientId(dto.getClientId())
                .setPortfolioId(dto.getPortfolioId())
                .setGoalId(dto.getGoalId())
                .setReferenceCode(dto.getReferenceCode())
                .setUpdateTime(DateUtils.now())
                .setCreateTime(DateUtils.now())
                .setId(Sequence.next());
        return userGoalInfo;
    }

    private UserGoalInfoDTO modelConvertToDto(UserGoalInfo userGoalInfo) {
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
    public void updateDeletedByGoalId(String goalId) {
        mapper.updateDeletedByGoalId(goalId);
    }

}
