package com.pivot.aham.api.service.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.UserInfoDTO;
import com.pivot.aham.api.service.mapper.UserInfoMapper;
import com.pivot.aham.api.service.mapper.model.UserInfo;
import com.pivot.aham.api.service.service.UserInfoService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DateUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by luyang.li on 18/12/2.
 */
@Service
public class UserInfoServiceImpl extends BaseServiceImpl<UserInfo, UserInfoMapper> implements UserInfoService {

    @Override
    public UserInfo queryByClientId(String clientId) {
        return mapper.queryByClientId(clientId);
    }

    @Override
    public void saveUserInfo(UserInfoDTO userInfoDTO) {
        UserInfo userInfo = dtoConvertToModel(userInfoDTO);
        mapper.saveUserInfo(userInfo);
    }

    @Override
    public int updateUserInfo(UserInfoDTO userInfoDTO) {
        UserInfo userInfo = dtoConvertToModel(userInfoDTO);
        return mapper.updateUserInfo(userInfo);
    }

    @Override
    public List<UserInfoDTO> queryListByClients(List<String> clientIds) {
        List<UserInfo> userInfoList = mapper.queryListByClients(clientIds);
        return userInfoList.stream().map(this::modelConvertToDto).collect(Collectors.toList());
    }

    @Override
    public void saveBatch(List<UserInfoDTO> userInfoDTOList) {
        List<UserInfo> userInfoList = userInfoDTOList.stream().map(this::dtoConvertToModel).collect(Collectors.toList());
        mapper.saveBatch(userInfoList);
    }

    @Override
    public Page<UserInfoDTO> listUserInfoPage(Page<UserInfoDTO> rowBounds, UserInfoDTO userInfoDTO) {
        UserInfo userInfo = dtoConvertToModel(userInfoDTO);
        List<UserInfo> userInfoList = mapper.listUserInfoPage(rowBounds,userInfo);

        List<UserInfoDTO> userInfoDTOList = Lists.newArrayList();
        for(UserInfo userInfo1:userInfoList){
            UserInfoDTO userInfoDTO1 = BeanMapperUtils.map(userInfo1,UserInfoDTO.class);
            userInfoDTOList.add(userInfoDTO1);
        }
        rowBounds.setRecords(userInfoDTOList);

        return rowBounds;
    }

    private UserInfo dtoConvertToModel(UserInfoDTO dto) {
        UserInfo userInfo = new UserInfo();
        userInfo.setClientId(dto.getClientId())
                .setClientName(dto.getClientName())
                .setLikeClientId(dto.getLikeClientId())
                .setLikeClientName(dto.getLikeClientName())
                .setAddress(dto.getAddress())
                .setMobileNum(dto.getMobileNum())
                .setUpdateTime(DateUtils.now())
                .setCreateTime(DateUtils.now())
                .setId(Sequence.next());
        return userInfo;
    }

    private UserInfoDTO modelConvertToDto(UserInfo userInfo) {
        UserInfoDTO dto = new UserInfoDTO();
        dto.setClientId(userInfo.getClientId())
                .setClientName(userInfo.getClientName())
                .setCreateTime(userInfo.getCreateTime());
        dto.setUpdateTime(userInfo.getUpdateTime());
        return dto;
    }

}
