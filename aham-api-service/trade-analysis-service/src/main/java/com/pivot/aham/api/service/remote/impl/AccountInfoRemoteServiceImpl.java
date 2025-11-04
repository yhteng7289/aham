package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.AccountInfoReqDTO;
import com.pivot.aham.api.server.dto.res.AccountInfoResDTO;
import com.pivot.aham.api.server.remoteservice.AccountInfoRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.service.AccountInfoService;
import com.pivot.aham.api.service.service.AccountUserService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.enums.AccountTypeEnum;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.analysis.InitDayEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

@Service(interfaceClass = AccountInfoRemoteService.class)
@Slf4j
public class AccountInfoRemoteServiceImpl implements AccountInfoRemoteService {

    @Resource
    private AccountInfoService accountInfoService;
    
    @Resource
    private AccountUserService acountUserService;

    @Override
    public RpcMessage<Page<AccountInfoResDTO>> getAccountInfoPage(AccountInfoReqDTO accountInfoReqDTO) {

        Page<AccountInfoPO> rowBounds = new Page<>(accountInfoReqDTO.getPageNo(), accountInfoReqDTO.getPageSize());
        AccountInfoPO accountInfoQuery = BeanMapperUtils.map(accountInfoReqDTO, AccountInfoPO.class);
        Page<AccountInfoPO> pagination = accountInfoService.listPageAccountInfo(accountInfoQuery, rowBounds);
        List<AccountInfoPO> accountInfoPOList = pagination.getRecords();

        Page<AccountInfoResDTO> paginationRes = new Page<>();
        paginationRes = BeanMapperUtils.map(pagination, paginationRes.getClass());
        List<AccountInfoResDTO> resRecords = BeanMapperUtils.mapList(accountInfoPOList, AccountInfoResDTO.class);
        paginationRes.setRecords(resRecords);

        return RpcMessage.success(paginationRes);
    }

    @Override
    public AccountInfoResDTO createNewAccIfNotExist(AccountInfoResDTO accountInfoResDTO) {
        AccountInfoPO accountInfoPO = new AccountInfoPO();
        populateAccountInfoPOFromDTO(accountInfoPO, accountInfoResDTO);
        accountInfoPO = accountInfoService.queryAccountInfo(accountInfoPO);
        AccountInfoResDTO accInfoResDTO = new AccountInfoResDTO();
        if(accountInfoPO == null){
            AccountInfoPO accInfoPO = new AccountInfoPO();
            accInfoPO.setPortfolioId(accountInfoResDTO.getPortfolioId());
            accInfoPO.setGoalId(accountInfoResDTO.getGoalId());
            accInfoPO.setInvestType(AccountTypeEnum.TAILOR);
            accInfoPO.setInitDay(InitDayEnum.INIT_DAY);
            accountInfoService.insert(accInfoPO);
            accInfoPO = accountInfoService.queryAccountInfo(accInfoPO);
            AccountUserPO accountUserPO = new AccountUserPO();
            accountUserPO.setAccountId(accInfoPO.getId());
            accountUserPO.setClientId(accountInfoResDTO.getClientId());
            accountUserPO.setPortfolioId(accInfoPO.getPortfolioId());
            accountUserPO.setGoalId(accInfoPO.getGoalId());
            accountUserPO.setReferenceCode(accountInfoResDTO.getReferenceCode());
            acountUserService.insertAccountUser(accountUserPO);

            accInfoResDTO.setId(accInfoPO.getId());
            accInfoResDTO.setGoalId(accInfoPO.getGoalId());
            accInfoResDTO.setPortfolioId(accInfoPO.getPortfolioId());
            
        }else{
            accInfoResDTO.setId(accountInfoPO.getId());
            accInfoResDTO.setGoalId(accountInfoPO.getGoalId());
            accInfoResDTO.setPortfolioId(accountInfoPO.getPortfolioId());
            
        }
        return accInfoResDTO;
    }

    private void populateAccountInfoPOFromDTO(AccountInfoPO accountInfoPO, AccountInfoResDTO accountInfoResDTO){

        if (accountInfoResDTO != null){
            accountInfoPO.setPortfolioId(accountInfoResDTO.getPortfolioId());
            accountInfoPO.setInvestType(accountInfoResDTO.getInvestType());
            accountInfoPO.setInitDay(accountInfoResDTO.getInitDay());
            accountInfoPO.setGoalId(accountInfoResDTO.getGoalId());
            if (accountInfoResDTO.getId() != null){
                accountInfoPO.setId(accountInfoResDTO.getId());
            }
        }
    }
}
