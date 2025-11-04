package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.UserStaticsReqDTO;
import com.pivot.aham.api.server.dto.UserStaticsResDTO;
import com.pivot.aham.api.server.dto.req.UserEtfSharesReqDTO;
import com.pivot.aham.api.server.dto.req.UserProfitInfoReqDTO;
import com.pivot.aham.api.server.dto.res.UserEtfSharesResDTO;
import com.pivot.aham.api.server.dto.res.UserProfitInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserStaticsRemoteService;
import com.pivot.aham.api.service.mapper.model.UserEtfSharesPO;
import com.pivot.aham.api.service.mapper.model.UserProfitInfoPO;
import com.pivot.aham.api.service.mapper.model.UserStaticsPO;
import com.pivot.aham.api.service.service.UserEtfSharesService;
import com.pivot.aham.api.service.service.UserProfitInfoService;
import com.pivot.aham.api.service.service.UserStaticsService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;


/**
 * 用户统计信息
 *
 * @author addison
 * @since 2018年12月10日
 */
@Service(interfaceClass = UserStaticsRemoteService.class)
@Slf4j
public class UserStaticsRemoteServiceImpl implements UserStaticsRemoteService {

    @Resource
    private UserStaticsService userStaticsService;
    @Resource
    private UserProfitInfoService userProfitInfoService;
    @Resource
    private UserEtfSharesService userEtfSharesService;

    @Override
    public RpcMessage<List<UserStaticsResDTO>> getUserStatics(UserStaticsReqDTO userStaticsReqDTO) {
        UserStaticsPO userStaticsQuery = new UserStaticsPO();
        userStaticsQuery.setAccountId(userStaticsReqDTO.getAccountId());
        userStaticsQuery.setGoalId(userStaticsReqDTO.getGoalId());
        userStaticsQuery.setClientId(userStaticsReqDTO.getClientId());
        userStaticsQuery.setStaticDate(userStaticsReqDTO.getStaticDate());
        List<UserStaticsPO> userStaticsPOS = userStaticsService.queryUserStatics(userStaticsQuery);

        List<UserStaticsResDTO> userStaticsResDTOList = BeanMapperUtils.mapList(userStaticsPOS,UserStaticsResDTO.class);

        return RpcMessage.success(userStaticsResDTOList);
    }

    @Override
    public RpcMessage<UserStaticsResDTO> getUserStatic(UserStaticsReqDTO userStaticsReqDTO) {
        UserStaticsPO userStaticsPO = new UserStaticsPO();
        userStaticsPO.setClientId(userStaticsReqDTO.getClientId());
        userStaticsPO.setGoalId(userStaticsReqDTO.getGoalId());
        userStaticsPO.setAccountId(userStaticsReqDTO.getAccountId());
        userStaticsPO.setStaticDate(userStaticsReqDTO.getStaticDate());
        UserStaticsPO userStatics = userStaticsService.selectByStaticDate(userStaticsPO);
        UserStaticsResDTO userStaticsResDTO = BeanMapperUtils.map(userStatics,UserStaticsResDTO.class);
        return RpcMessage.success(userStaticsResDTO);
    }

    @Override
    public RpcMessage<List<UserProfitInfoResDTO>> getUserProfitInfos(UserProfitInfoReqDTO userProfitInfoReqDTO) {
        UserProfitInfoPO userProfitInfoQuery = new UserProfitInfoPO();
        userProfitInfoQuery = BeanMapperUtils.map(userProfitInfoReqDTO,UserProfitInfoPO.class);
        List<UserProfitInfoPO> userProfitInfoResDTOList = userProfitInfoService.queryProfitList(userProfitInfoQuery);

        List<UserProfitInfoResDTO> userProfitInfoResList = BeanMapperUtils.mapList(userProfitInfoResDTOList,UserProfitInfoResDTO.class);

        return RpcMessage.success(userProfitInfoResList);
    }

    @Override
    public RpcMessage<List<UserEtfSharesResDTO>> getUserEtfShares(UserEtfSharesReqDTO userEtfSharesReqDTO) {
        UserEtfSharesPO userEtfSharesPO = BeanMapperUtils.map(userEtfSharesReqDTO,UserEtfSharesPO.class);
        List<UserEtfSharesPO> userEtfSharesPOList = userEtfSharesService.queryListByTime(userEtfSharesPO);
        List<UserEtfSharesResDTO> userEtfSharesResDTOList = BeanMapperUtils.mapList(userEtfSharesPOList,UserEtfSharesResDTO.class);

        return RpcMessage.success(userEtfSharesResDTOList);
    }
}
