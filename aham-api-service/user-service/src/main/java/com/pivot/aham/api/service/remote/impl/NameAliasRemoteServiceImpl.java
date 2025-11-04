package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.BankNameAliasReqDTO;
import com.pivot.aham.api.server.dto.BankNameAliasResDTO;
import com.pivot.aham.api.server.remoteservice.NameAliasRemoteService;
import com.pivot.aham.api.service.mapper.model.BankNameAlias;
import com.pivot.aham.api.service.service.BankNameAliasService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.enums.NameAliasEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;



/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月29日
 */
@Service(interfaceClass = NameAliasRemoteService.class)
@Slf4j
public class NameAliasRemoteServiceImpl implements NameAliasRemoteService {

   @Resource
    private BankNameAliasService bankNameAliasService;
    @Override
    public RpcMessage<Page<BankNameAliasResDTO>> getBankNameAliasPage(BankNameAliasReqDTO bankNameAliasReqDTO) {

        Page<BankNameAlias> rowBounds = new Page<BankNameAlias>(
                bankNameAliasReqDTO.getPageNo(),bankNameAliasReqDTO.getPageSize());
        BankNameAlias oBankNameAlias = BeanMapperUtils.map(bankNameAliasReqDTO,BankNameAlias.class);
        oBankNameAlias.setStatus(NameAliasEnum.PENDING);
        Page<BankNameAlias> pagination = bankNameAliasService.listPageBankNameAlias(oBankNameAlias,rowBounds);
        List<BankNameAlias> bankNameAliasList = pagination.getRecords();

        Page<BankNameAliasResDTO> paginationRes = new Page<>();
        paginationRes=BeanMapperUtils.map(pagination,paginationRes.getClass());
        List<BankNameAliasResDTO> resRecords = BeanMapperUtils.mapList(bankNameAliasList,BankNameAliasResDTO.class);
        paginationRes.setRecords(resRecords);

        return RpcMessage.success(paginationRes);
    }
    
    @Override
    public RpcMessage<BankNameAliasResDTO> queryClientInfo(String rechargeId) { 
        BankNameAlias bankNameAlias = new BankNameAlias();
        //bankNameAlias.setClientId(clientId);
        bankNameAlias.setRechargeId(rechargeId);
        bankNameAlias = bankNameAliasService.queryClientInfo(bankNameAlias);
        
        BankNameAliasResDTO bankNameAliasResDTO = new BankNameAliasResDTO();
        bankNameAliasResDTO.setSysClientName(bankNameAlias.getSysClientName());
        bankNameAliasResDTO.setClientId(bankNameAlias.getClientId());
        bankNameAliasResDTO.setVirtualAccountNo(bankNameAlias.getVirtualAccountNo());
        bankNameAliasResDTO.setRechargeId(bankNameAlias.getRechargeId());
        return RpcMessage.success(bankNameAliasResDTO);
    }
    
     @Override
     public void approvedNameAlias(BankNameAliasReqDTO bankNameAliasReqDTO){
          BankNameAlias bankNameAlias = new BankNameAlias();
          if(bankNameAliasReqDTO.getFileName1() != null && !bankNameAliasReqDTO.getFileName1().equalsIgnoreCase("")){
            bankNameAlias.setFileName1(bankNameAliasReqDTO.getFileName1());
          }
          if(bankNameAliasReqDTO.getFileName2() != null && !bankNameAliasReqDTO.getFileName2().equalsIgnoreCase("")){
            bankNameAlias.setFileName2(bankNameAliasReqDTO.getFileName2());
          }
          bankNameAlias.setStatus(bankNameAliasReqDTO.getStatus());
          bankNameAlias.setRechargeId(bankNameAliasReqDTO.getRechargeId());
          bankNameAliasService.updateApprovedNameAlias(bankNameAlias);
     }
     
     @Override
     public void updateRejection(BankNameAliasReqDTO bankNameAliasReqDTO){
          BankNameAlias bankNameAlias = new BankNameAlias();
          bankNameAlias.setRechargeId(bankNameAliasReqDTO.getRechargeId());
          bankNameAlias.setReasonRejected(bankNameAliasReqDTO.getReasonRejection());
          bankNameAlias.setStatus(NameAliasEnum.REJECTED);
          bankNameAliasService.updateApprovedNameAlias(bankNameAlias);
     }
    
}
