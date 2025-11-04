package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.remoteservice.UobSaveRechargeLogRemoteService;
import com.pivot.aham.api.service.UobTradingService;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import com.pivot.aham.api.server.dto.UobRechargeLogDTO;
import com.pivot.aham.api.server.dto.req.UobRechargeReq;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

/**
 * Created by dexter on 17/4/2020
 */
@Service(interfaceClass = UobSaveRechargeLogRemoteService.class)
@Slf4j
public class UobSaveRechargeLogRemoteServiceImpl implements UobSaveRechargeLogRemoteService {

    @Resource
    private UobTradingService uobTradingService;

    @Override
    public String runRechargeLog() {
        
        uobTradingService.saveRechargeLog();

        return "Running";
    }
    
    @Override
    public RpcMessage<String> insertRechargeLog(List<UobRechargeReq> uobRechargeReq) {
        
        List<UobRechargeLogDTO> rechargeList = Lists.newArrayList();
        
        for (UobRechargeReq rechargeReq : uobRechargeReq) {
        
            UobRechargeLogDTO uobRechargeLogDTO = new UobRechargeLogDTO();
            
            uobRechargeLogDTO.setBankOrderNo(rechargeReq.getBankOrderNo());
            uobRechargeLogDTO.setClientName(rechargeReq.getClientName());
            uobRechargeLogDTO.setVirtualAccountNo(rechargeReq.getVirtualAccountNo());
            uobRechargeLogDTO.setCurrency(rechargeReq.getCurrency());
            uobRechargeLogDTO.setReferenceCode(rechargeReq.getReferenceCode());
            uobRechargeLogDTO.setCashAmount(rechargeReq.getCashAmount());
            uobRechargeLogDTO.setTradeTime(rechargeReq.getTradeTime());
        
            rechargeList.add(uobRechargeLogDTO);
        }
        
        RpcMessage<String> statusSave =  uobTradingService.insertRechargeLog(rechargeList); // Edit By WooiTatt
        if(statusSave.isSuccess()){
            return RpcMessage.success("Successfully");
        }else{
            return RpcMessage.error(statusSave.getErrMsg());
        }
        
    }
    
    @Override
    public RpcMessage<String> insertOneRechargeLog(UobRechargeReq uobRechargeReq) {
        
        List<UobRechargeLogDTO> rechargeList = Lists.newArrayList();
        
        UobRechargeLogDTO uobRechargeLogDTO = new UobRechargeLogDTO();
            
        uobRechargeLogDTO.setBankOrderNo(uobRechargeReq.getBankOrderNo());
        uobRechargeLogDTO.setClientName(uobRechargeReq.getClientName());
        uobRechargeLogDTO.setVirtualAccountNo(uobRechargeReq.getVirtualAccountNo());
        uobRechargeLogDTO.setCurrency(uobRechargeReq.getCurrency());
        uobRechargeLogDTO.setReferenceCode(uobRechargeReq.getReferenceCode());
        uobRechargeLogDTO.setCashAmount(uobRechargeReq.getCashAmount());
        uobRechargeLogDTO.setTradeTime(uobRechargeReq.getTradeTime());

        rechargeList.add(uobRechargeLogDTO);
        
        RpcMessage<String> statusSave =  uobTradingService.insertRechargeLog(rechargeList); // Edit By WooiTatt
        if(statusSave.isSuccess()){
            return RpcMessage.success("Successfully");
        }else{
            return RpcMessage.error(statusSave.getErrMsg());
        }
        
    }
    
}
