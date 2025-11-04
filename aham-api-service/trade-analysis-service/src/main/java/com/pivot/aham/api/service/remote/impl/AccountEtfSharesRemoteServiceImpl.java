package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.req.AccountetfSharesReqDTO;
import com.pivot.aham.api.server.dto.res.AccountetfSharesResDTO;
import com.pivot.aham.api.server.remoteservice.AccountEtfSharesRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.api.service.service.AccountEtfSharesService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

@Service(interfaceClass = AccountEtfSharesRemoteService.class)
@Slf4j
public class AccountEtfSharesRemoteServiceImpl implements AccountEtfSharesRemoteService {

    @Resource
    private AccountEtfSharesService accountEtfSharesService;

    @Override
    public RpcMessage<List<AccountetfSharesResDTO>> getAccountEtfShares(AccountetfSharesReqDTO accountetfSharesReqDTO) {
        AccountEtfSharesPO accountEtfSharesQuery = BeanMapperUtils.map(accountetfSharesReqDTO, AccountEtfSharesPO.class);
        List<AccountEtfSharesPO> accountEtfSharesList = accountEtfSharesService.queryList(accountEtfSharesQuery);
        List<AccountetfSharesResDTO> accountetfSharesResDTOList = BeanMapperUtils.mapList(accountEtfSharesList, AccountetfSharesResDTO.class);
        return RpcMessage.success(accountetfSharesResDTOList);
    }

    @Override
    public RpcMessage<List<AccountetfSharesResDTO>> selectByStaticDate(AccountetfSharesReqDTO accountetfSharesReqDTO) {
        AccountEtfSharesPO accountEtfSharesPO = new AccountEtfSharesPO();
        accountEtfSharesPO.setAccountId(accountetfSharesReqDTO.getAccountId());
        accountEtfSharesPO.setStaticDate(accountetfSharesReqDTO.getStaticDate());
        List<AccountEtfSharesPO> accountEtfSharesList = accountEtfSharesService.selectByStaticDate(accountEtfSharesPO);
        List<AccountetfSharesResDTO> accountetfSharesResDTOList = BeanMapperUtils.mapList(accountEtfSharesList, AccountetfSharesResDTO.class);
        return RpcMessage.success(accountetfSharesResDTOList);
    }

    @Override
    public RpcMessage<AccountetfSharesResDTO> selectByStaticDateByAccountId(AccountetfSharesReqDTO accountetfSharesReqDTO) {
        AccountEtfSharesPO accountEtfSharesQuery = BeanMapperUtils.map(accountetfSharesReqDTO, AccountEtfSharesPO.class);
        AccountEtfSharesPO accountEtfSharesPO = accountEtfSharesService.selectByStaticDateByAccountId(accountEtfSharesQuery);
        AccountetfSharesResDTO accountetfSharesResDTO = BeanMapperUtils.map(accountEtfSharesPO, AccountetfSharesResDTO.class);
        return RpcMessage.success(accountetfSharesResDTO);
    }

}
