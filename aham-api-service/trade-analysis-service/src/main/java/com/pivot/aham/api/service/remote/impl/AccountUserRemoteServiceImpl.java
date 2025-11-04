package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.req.AccountUserReqDTO;
import com.pivot.aham.api.server.dto.res.AccountUserResDTO;
import com.pivot.aham.api.server.remoteservice.AccountUserRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.service.AccountUserService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;


@Service(interfaceClass = AccountUserRemoteService.class)
@Slf4j
public class AccountUserRemoteServiceImpl implements AccountUserRemoteService {

    @Resource
    private AccountUserService accountUserService;
    @Override
    public RpcMessage<List<AccountUserResDTO>> getAccountUserList(AccountUserReqDTO accountUserReqDTO) {
        AccountUserPO accountUserQuery = BeanMapperUtils.map(accountUserReqDTO,AccountUserPO.class);
        List<AccountUserPO> accountUserList = accountUserService.queryList(accountUserQuery);

        List<AccountUserResDTO> accountUserResDTOList = BeanMapperUtils.mapList(accountUserList,AccountUserResDTO.class);
        return RpcMessage.success(accountUserResDTOList);
    }
}
