package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.req.AccountStaticsReqDTO;
import com.pivot.aham.api.server.dto.res.AccountStaticsResDTO;
import com.pivot.aham.api.server.remoteservice.AccountStaticsRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountStaticsPO;
import com.pivot.aham.api.service.service.AccountStaticsService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

@Service(interfaceClass = AccountStaticsRemoteService.class)
@Slf4j
public class AccountStaticsRemoteServiceImpl implements AccountStaticsRemoteService {

    @Resource
    private AccountStaticsService accountStaticsService;

    @Override
    public RpcMessage<List<AccountStaticsResDTO>> getUserStatics(AccountStaticsReqDTO accountStaticsReqDTO) {

        AccountStaticsPO accountStaticsQuery = BeanMapperUtils.map(accountStaticsReqDTO, AccountStaticsPO.class);
        List<AccountStaticsPO> accountStaticsPOList = accountStaticsService.queryList(accountStaticsQuery);

        List<AccountStaticsResDTO> accountStaticsResDTOList = BeanMapperUtils.mapList(accountStaticsPOList, AccountStaticsResDTO.class);

        return RpcMessage.success(accountStaticsResDTOList);
    }

    @Override
    public RpcMessage<AccountStaticsResDTO> getLastUserStatics(AccountStaticsReqDTO accountStaticsReqDTO) {
        AccountStaticsPO accountStatics = new AccountStaticsPO();
        accountStatics.setAccountId(accountStaticsReqDTO.getAccountId());
        AccountStaticsPO accountStaticsPO = accountStaticsService.selectLastStatic(accountStatics);

        AccountStaticsResDTO accountStaticsResDTO = BeanMapperUtils.map(accountStaticsPO, AccountStaticsResDTO.class);
        return RpcMessage.success(accountStaticsResDTO);
    }

    @Override
    public RpcMessage<AccountStaticsResDTO> selectByStaticDate(AccountStaticsReqDTO accountStaticsReqDTO) {

        AccountStaticsPO accountStaticsPO = BeanMapperUtils.map(accountStaticsReqDTO, AccountStaticsPO.class);

        AccountStaticsPO accountStatics = accountStaticsService.selectByStaticDate(accountStaticsPO);

        AccountStaticsResDTO accountStaticsResDTO = new AccountStaticsResDTO();
        if(accountStatics != null){ //Added WooiTatt 
            accountStaticsResDTO  = BeanMapperUtils.map(accountStatics, AccountStaticsResDTO.class);
        }

        return RpcMessage.success(accountStaticsResDTO);
    }

}
