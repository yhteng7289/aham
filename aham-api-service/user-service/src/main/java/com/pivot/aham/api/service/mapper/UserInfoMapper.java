package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.UserInfoDTO;
import com.pivot.aham.api.service.mapper.model.UserInfo;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserInfoMapper extends BaseMapper<UserInfo> {

    UserInfo queryByClientId(@Param("clientId") String clientId);

    void saveUserInfo(UserInfo userInfo);

    int updateUserInfo(UserInfo userInfo);

    List<UserInfo> queryListByClients(@Param("clientIds") List<String> clientIds);

    void saveBatch(List<UserInfo> userInfoList);

    List<UserInfo> listUserInfoPage(Page<UserInfoDTO> rowBounds, UserInfo userInfo);

}