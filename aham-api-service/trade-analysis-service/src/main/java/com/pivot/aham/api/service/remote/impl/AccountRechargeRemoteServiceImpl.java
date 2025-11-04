package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.AccountRechargeVoDTO;
import com.pivot.aham.api.server.remoteservice.AccountRechargeRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.service.AccountRechargeService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

@Service(interfaceClass = AccountRechargeRemoteService.class)
@Slf4j
public class AccountRechargeRemoteServiceImpl implements AccountRechargeRemoteService {

    @Resource
    private AccountRechargeService accountRechargeService;

    @Override
    public RpcMessage<BigDecimal> getSumAccountRecharge() {
        //AccountEtfSharesPO accountEtfSharesQuery = BeanMapperUtils.map(accountetfSharesReqDTO, AccountEtfSharesPO.class);
        
        BigDecimal totalRecharge = BigDecimal.ZERO;
        try{
            totalRecharge = accountRechargeService.getSumAccountRecharge();
        //List<AccountRechargeVoDTO> listAccountRechargeDTO = BeanMapperUtils.mapList(listAccountRechargePo, AccountRechargeVoDTO.class);
        }catch(Exception e){log.error(e.toString());}
        return RpcMessage.success(totalRecharge);
    }
    
    @Override
    public RpcMessage<BigDecimal> getSumAccRechargeByGoalClient(AccountRechargeVoDTO accountRechargeVoDTO) {
        //AccountEtfSharesPO accountEtfSharesQuery = BeanMapperUtils.map(accountetfSharesReqDTO, AccountEtfSharesPO.class);
        
        BigDecimal totalRecharge = BigDecimal.ZERO;
        AccountRechargePO accountRechargePO = BeanMapperUtils.map(accountRechargeVoDTO, AccountRechargePO.class);
        try{
            totalRecharge = accountRechargeService.getSumAccRechargeByGoalClient(accountRechargePO);
        //List<AccountRechargeVoDTO> listAccountRechargeDTO = BeanMapperUtils.mapList(listAccountRechargePo, AccountRechargeVoDTO.class);
        }catch(Exception e){log.error(e.toString());}
        return RpcMessage.success(totalRecharge);
    }

}
