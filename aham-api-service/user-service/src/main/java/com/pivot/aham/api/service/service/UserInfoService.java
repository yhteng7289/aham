package com.pivot.aham.api.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.UserInfoDTO;
import com.pivot.aham.api.service.mapper.model.UserInfo;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

public interface UserInfoService extends BaseService<UserInfo> {

    /**
     * 根据clientId查询用户信息
     *
     * @param clientId
     * @return
     */
    UserInfo queryByClientId(String clientId);

    /**
     * 保存用户信息
     *
     * @param userInfoDTO
     */
    void saveUserInfo(UserInfoDTO userInfoDTO);

    /**
     * 更新用户信息
     *
     * @param userInfoDTO
     * @return
     */
    int updateUserInfo(UserInfoDTO userInfoDTO);

    /**
     * 更具client批量查询用户
     *
     * @param clientIds
     * @return
     */
    List<UserInfoDTO> queryListByClients(List<String> clientIds);

    /**
     * 批量保存用户基础信息
     *
     * @param userInfoDTOList
     */
    void saveBatch(List<UserInfoDTO> userInfoDTOList);


    Page<UserInfoDTO> listUserInfoPage(Page<UserInfoDTO> rowBounds, UserInfoDTO userInfoDTO);

}
