package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.AccountRedeemVoDTO;
import com.pivot.aham.api.server.dto.req.AccountetfSharesReqDTO;
import com.pivot.aham.api.server.dto.res.AccountetfSharesResDTO;
import com.pivot.aham.api.server.remoteservice.AccountEtfSharesRemoteService;
import com.pivot.aham.api.server.remoteservice.AccountRedeemRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.api.service.service.AccountEtfSharesService;
import com.pivot.aham.api.service.service.AccountRedeemService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

@Service(interfaceClass = AccountRedeemRemoteService.class)
@Slf4j
public class AccountRedeemRemoteServiceImpl implements AccountRedeemRemoteService {

    @Resource
    private AccountRedeemService accountRedeemService;

    @Override
    public RpcMessage<BigDecimal> getSumRedeemConfirmAmount() {
        BigDecimal totalRedeem = BigDecimal.ZERO;
        try{
            totalRedeem = accountRedeemService.getSumRedeemConfirmAmount(); 
        }catch(Exception e){log.error(e.toString());}
        return RpcMessage.success(totalRedeem);
    }
    
    @Override
    public RpcMessage<BigDecimal> getSumRedeemConfirmAmtByGoalClient(AccountRedeemVoDTO accountRedeemVoDTO) {
        BigDecimal totalRedeem = BigDecimal.ZERO;
        AccountRedeemPO accountRedeemPO= BeanMapperUtils.map(accountRedeemVoDTO, AccountRedeemPO.class);
        try{
            totalRedeem = accountRedeemService.getSumRedeemConfirmAmtByGoalClient(accountRedeemPO); 
        }catch(Exception e){log.error(e.toString());}
        return RpcMessage.success(totalRedeem);
    }

}
