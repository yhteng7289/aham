package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.AccountBalanceAdjDetailReqDTO;
import com.pivot.aham.api.server.dto.req.AccountBalanceHisRecordReqDTO;
import com.pivot.aham.api.server.dto.req.BalanceRecordReqDTO;
import com.pivot.aham.api.server.dto.req.ReCalBuyEtfInBalReqDTO;
import com.pivot.aham.api.server.dto.res.AccountBalanceAdjDetailResDTO;
import com.pivot.aham.api.server.dto.res.AccountBalanceHisRecordResDTO;
import com.pivot.aham.api.server.dto.res.BalanceRecordResDTO;
import com.pivot.aham.api.server.remoteservice.AccountReBalanceRemoteService;
import com.pivot.aham.api.service.job.impl.rebalance.AdjustPlanBuyBuilder;
import com.pivot.aham.api.service.mapper.model.AccountBalanceAdjDetail;
import com.pivot.aham.api.service.mapper.model.AccountBalanceHisRecord;
import com.pivot.aham.api.service.mapper.model.AccountBalanceRecord;
import com.pivot.aham.api.service.service.AccountBalanceAdjDetailService;
import com.pivot.aham.api.service.service.AccountBalanceHisRecordService;
import com.pivot.aham.api.service.service.AccountBalanceRecordService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;


@Service(interfaceClass = AccountReBalanceRemoteService.class)
@Slf4j
public class AccountReBalanceRemoteServiceImpl implements AccountReBalanceRemoteService {
    @Resource
    private AdjustPlanBuyBuilder adjustPlanBuyBuilder;
    @Resource
    private AccountBalanceRecordService accountBalanceRecordService;
    @Resource
    private AccountBalanceAdjDetailService accountBalanceAdjDetailService;
    @Resource
    private AccountBalanceHisRecordService accountBalanceHisRecordService;

    @Override
    public RpcMessage reCalBuyEtfInBal(List<ReCalBuyEtfInBalReqDTO> reCalBuyEtfInBalReqDTOList) {
        adjustPlanBuyBuilder.setReCalBuyEtfInBalReqDTOList(reCalBuyEtfInBalReqDTOList);
        List<AccountBalanceAdjDetail> accountBalanceAdjDetailList = adjustPlanBuyBuilder.build();
        return RpcMessage.success(accountBalanceAdjDetailList);
    }

    @Override
    public RpcMessage checkReBalance(Long accountId) {
        return null;
    }

    @Override
    public RpcMessage<Page<BalanceRecordResDTO>> getBalanceRecords(BalanceRecordReqDTO balanceRecordReqDTO) {
        Page<AccountBalanceRecord> rowBounds = new Page<>(
                balanceRecordReqDTO.getPageNo(),balanceRecordReqDTO.getPageSize());
        AccountBalanceRecord accountBalanceRecord = BeanMapperUtils.map(balanceRecordReqDTO,AccountBalanceRecord.class);
        Page<AccountBalanceRecord> pagination = accountBalanceRecordService.getAccountBalRecordPage(rowBounds,accountBalanceRecord);

        Page<BalanceRecordResDTO> balanceRecordResDTOPagination = new Page<>();
        balanceRecordResDTOPagination = BeanMapperUtils.map(pagination,balanceRecordResDTOPagination.getClass());
        List<AccountBalanceRecord> accountBalanceRecords = pagination.getRecords();
        List<BalanceRecordResDTO> balanceRecordResDTOS = BeanMapperUtils.mapList(accountBalanceRecords,BalanceRecordResDTO.class);
        balanceRecordResDTOPagination.setRecords(balanceRecordResDTOS);

        return RpcMessage.success(balanceRecordResDTOPagination);
    }

    @Override
    public RpcMessage<List<AccountBalanceAdjDetailResDTO>> getBalanceAdjDetails(AccountBalanceAdjDetailReqDTO accountBalanceAdjDetailResDTO) {

        AccountBalanceAdjDetail accountBalanceAdjDetail = BeanMapperUtils.map(
                accountBalanceAdjDetailResDTO,AccountBalanceAdjDetail.class);
        List<AccountBalanceAdjDetail> accountBalanceAdjDetails = accountBalanceAdjDetailService.queryList(accountBalanceAdjDetail);

        List<AccountBalanceAdjDetailResDTO> accountBalanceAdjDetailResDTOS =
                BeanMapperUtils.mapList(accountBalanceAdjDetails,AccountBalanceAdjDetailResDTO.class);

        return RpcMessage.success(accountBalanceAdjDetailResDTOS);
    }

    @Override
    public RpcMessage<Page<AccountBalanceHisRecordResDTO>> getAccBalanceHisPage(AccountBalanceHisRecordReqDTO accountBalanceHisRecordReqDTO) {
        
        Page<AccountBalanceHisRecord> rowBounds = new Page<>(
                accountBalanceHisRecordReqDTO.getPageNo(),accountBalanceHisRecordReqDTO.getPageSize());
        
        AccountBalanceHisRecord accountBalanceHisRecord = BeanMapperUtils.map(accountBalanceHisRecordReqDTO,AccountBalanceHisRecord.class);
        Page<AccountBalanceHisRecord> pagination = accountBalanceHisRecordService.queryPageList(accountBalanceHisRecord, rowBounds);

        Page<AccountBalanceHisRecordResDTO> accountBalanceHisRecordResDTOPage = new Page<>();
        accountBalanceHisRecordResDTOPage = BeanMapperUtils.map(pagination,accountBalanceHisRecordResDTOPage.getClass());
        List<AccountBalanceHisRecord> accountBalanceHisRecordList = pagination.getRecords();
        List<AccountBalanceHisRecordResDTO> accountBalanceHisRecordResDTOList = 
                BeanMapperUtils.mapList(accountBalanceHisRecordList,AccountBalanceHisRecordResDTO.class);
        accountBalanceHisRecordResDTOPage.setRecords(accountBalanceHisRecordResDTOList);

        return RpcMessage.success(accountBalanceHisRecordResDTOPage);
    }

}
