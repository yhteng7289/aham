package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.remoteservice.SaxoStatisticRemoteService;
import com.pivot.aham.api.service.impl.SaxoStatisticServiceImpl;
import com.pivot.aham.api.service.mapper.model.SaxoOpenPositionStockPO;
import com.pivot.aham.api.service.mapper.model.SaxoReconBalCashPO;
import com.pivot.aham.api.service.mapper.model.SaxoShareOpenPositionPO;
import com.pivot.aham.api.service.mapper.model.SaxoShareTradePO;
import com.pivot.aham.api.service.SaxoStatisticService;
import com.pivot.aham.api.service.UobBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import com.pivot.aham.api.service.SaxoOpenPositionStockService;
import com.pivot.aham.api.service.SaxoShareOpenPositionService;
import com.pivot.aham.api.service.SaxoShareTradeService;

import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;

import java.util.Date;
import java.util.List;

/**
 * 提供应用
 * @author yi.zhang
 */
@Service(interfaceClass = SaxoStatisticRemoteService.class)
public class SaxoStatisticRemoteServiceImpl implements SaxoStatisticRemoteService{

    @Autowired
    private SaxoStatisticService saxoStatisticService;

    @Autowired
    private UobBalanceService uobBalanceService;
    
    @Autowired
    private SaxoShareOpenPositionService saxoShareOpenPositionService;
    
    @Autowired
    private SaxoShareTradeService saxoShareTradeService;
    
    @Autowired
    private SaxoOpenPositionStockService saxoOpenPosStkService;
    
    @Autowired
    private SaxoStatisticServiceImpl saxoStatisticService2;


    @Override
    public void dividend(Date nowDate) {
        saxoStatisticService.shareDividEnd(nowDate);
    }

    @Override
    public void totalStatisEnd(Date date) {
        saxoStatisticService.totalStatisEnd(date);
    }

    @Override
    public void recordBookkeepingCash() {
        saxoStatisticService.recordBookkeepingCash();
    }

    @Override
    public void statisShareTrades() {
        saxoStatisticService.statisShareTrades();
    }

    @Override
    public void statisShareOpenPositions() {
        saxoStatisticService.statisShareOpenPositions();
    }

    @Override
    public void recordCashTransactions() {
        saxoStatisticService.recordCashTransactions();
    }

    @Override
    public void statisExport() {
        uobBalanceService.statisExport();
    }

    @Override
    public void balanceOfAccount() {
//        saxoStatisticService.balanceOfAccount();
    }
    
    //Added By WooiTatt
    @Override
    public RpcMessage <Page<SaxoReconBalanceResDTO>> saxoReconBalance(SaxoReconBalanceReqDTO saxoReconBalanceReqDTO) {
        Page<SaxoReconBalCashPO> rowBounds = new Page<>(saxoReconBalanceReqDTO.getPageNo(), saxoReconBalanceReqDTO.getPageSize());
        Date startCreateTime = saxoReconBalanceReqDTO.getStartCreateTime();
        Date endCreateTime = saxoReconBalanceReqDTO.getEndCreateTime();
       
        SaxoReconBalCashPO saxoBalCashPO = BeanMapperUtils.map(saxoReconBalanceReqDTO, SaxoReconBalCashPO.class);
        
        Page<SaxoReconBalCashPO> saxoBalCashPagePO = saxoStatisticService2.saxoReconBalanceCash(saxoBalCashPO, rowBounds,startCreateTime, endCreateTime);
        Page<SaxoReconBalanceResDTO> paginationRes = new Page<>();
        paginationRes = BeanMapperUtils.map(saxoBalCashPagePO, paginationRes.getClass());
        List<SaxoReconBalCashPO> saxoBalCashPOList = saxoBalCashPagePO.getRecords();
        List<SaxoReconBalanceResDTO> userInfoResDTOList = BeanMapperUtils.mapList(saxoBalCashPOList, SaxoReconBalanceResDTO.class);
        paginationRes.setRecords(userInfoResDTOList);
        return RpcMessage.success(paginationRes);
    }
    
   @Override
    public RpcMessage <Page<SaxoOpenPositionResDTO>> saxoOpenPositionStock(SaxoOpenPositionReqDTO saxoOpenPositionReqDTO) {
        Page<SaxoOpenPositionStockPO> rowBounds = new Page<>(saxoOpenPositionReqDTO.getPageNo(), saxoOpenPositionReqDTO.getPageSize());
        Date startCreateTime = saxoOpenPositionReqDTO.getStartCreateTime();
        Date endCreateTime = saxoOpenPositionReqDTO.getEndCreateTime();
       
        SaxoOpenPositionStockPO saxoOpenPosStkPO = BeanMapperUtils.map(saxoOpenPositionReqDTO, SaxoOpenPositionStockPO.class);
        
        Page<SaxoOpenPositionStockPO> saxoOpenPosStkPage = saxoOpenPosStkService.saxoOpenPositionStock(saxoOpenPosStkPO, rowBounds,startCreateTime, endCreateTime);
        Page<SaxoOpenPositionResDTO> paginationRes = new Page<>();
        paginationRes = BeanMapperUtils.map(saxoOpenPosStkPage, paginationRes.getClass());
        List<SaxoOpenPositionStockPO> lSaxoOpenPositionStkPO = saxoOpenPosStkPage.getRecords();
        List<SaxoOpenPositionResDTO> userInfoResDTOList = BeanMapperUtils.mapList(lSaxoOpenPositionStkPO, SaxoOpenPositionResDTO.class);
        paginationRes.setRecords(userInfoResDTOList);
        return RpcMessage.success(paginationRes);
    }
    
    @Override
    public RpcMessage <Page<SaxoShareTradeResDTO>> saxoShareTrade(SaxoShareTradeReqDTO saxoShareTradeReqDTO) {
        Page<SaxoShareTradePO> rowBounds = new Page<>(saxoShareTradeReqDTO.getPageNo(), saxoShareTradeReqDTO.getPageSize());
        Date startCreateTime = saxoShareTradeReqDTO.getStartCreateTime();
        Date endCreateTime = saxoShareTradeReqDTO.getEndCreateTime();
       
        SaxoShareTradePO saxoShareTradePO = BeanMapperUtils.map(saxoShareTradeReqDTO, SaxoShareTradePO.class);
        
        Page<SaxoShareTradePO> saxoShareTradePage = saxoShareTradeService.saxoShareTrade(saxoShareTradePO, rowBounds,startCreateTime, endCreateTime);
        Page<SaxoShareTradeResDTO> paginationRes = new Page<>();
        paginationRes = BeanMapperUtils.map(saxoShareTradePage, paginationRes.getClass());
        List<SaxoShareTradePO> lSaxoShareTradePO = saxoShareTradePage.getRecords();
        List<SaxoShareTradeResDTO> userInfoResDTOList = BeanMapperUtils.mapList(lSaxoShareTradePO, SaxoShareTradeResDTO.class);
        paginationRes.setRecords(userInfoResDTOList);
        return RpcMessage.success(paginationRes);
    }
    
    @Override
    public RpcMessage <Page<SaxoShareOpenPositionResDTO>> saxoShareOpenPosition(SaxoShareOpenPositionReqDTO saxoShareOpenPositionReqDTO) {
        Page<SaxoShareOpenPositionPO> rowBounds = new Page<>(saxoShareOpenPositionReqDTO.getPageNo(), saxoShareOpenPositionReqDTO.getPageSize());
        Date startCreateTime = saxoShareOpenPositionReqDTO.getStartCreateTime();
        Date endCreateTime = saxoShareOpenPositionReqDTO.getEndCreateTime();
       
        SaxoShareOpenPositionPO saxoShareOpenPositionPO = BeanMapperUtils.map(saxoShareOpenPositionReqDTO, SaxoShareOpenPositionPO.class);
        
        Page<SaxoShareOpenPositionPO> saxoShareOpenPositionPage = saxoShareOpenPositionService.saxoShareopenPositionService(saxoShareOpenPositionPO, rowBounds,startCreateTime, endCreateTime);
        Page<SaxoShareOpenPositionResDTO> paginationRes = new Page<>();
        paginationRes = BeanMapperUtils.map(saxoShareOpenPositionPage, paginationRes.getClass());
        List<SaxoShareOpenPositionPO> lSaxoShareOpenPositionPO = saxoShareOpenPositionPage.getRecords();
        List<SaxoShareOpenPositionResDTO> userInfoResDTOList = BeanMapperUtils.mapList(lSaxoShareOpenPositionPO, SaxoShareOpenPositionResDTO.class);
        paginationRes.setRecords(userInfoResDTOList);
        return RpcMessage.success(paginationRes);
    }

    @Override
    public RpcMessage<List<SaxoShareTradeResDTO>> saxoShareTradeRemoteExport(SaxoShareTradeReqDTO saxoShareTradeReqDTO) {

        
        SaxoShareTradePO saxoShareTradePO = new SaxoShareTradePO();
        saxoShareTradePO.setStartDate(saxoShareTradeReqDTO.getStartCreateTime());
        saxoShareTradePO.setEndDate(saxoShareTradeReqDTO.getEndCreateTime());
        List<SaxoShareTradePO> lSaxoShareTradePO = 
                saxoShareTradeService.querySaxoShareTradeList(saxoShareTradePO);
        
        List<SaxoShareTradeResDTO> lSaxoShareTradeResDTO = BeanMapperUtils.mapList(lSaxoShareTradePO,SaxoShareTradeResDTO.class);
        return RpcMessage.success(lSaxoShareTradeResDTO);
    }
    
    @Override
    public RpcMessage<List<SaxoReconBalanceResDTO>> saxoReconBalanceRemoteExport(SaxoReconBalanceReqDTO saxoReconBalanceReqDTO) {

        
        SaxoReconBalCashPO saxoReconBalCashPO = new SaxoReconBalCashPO();
        saxoReconBalCashPO.setStartDate(saxoReconBalanceReqDTO.getStartCreateTime());
        saxoReconBalCashPO.setEndDate(saxoReconBalanceReqDTO.getEndCreateTime());
        List<SaxoReconBalCashPO> lSaxoReconBalCashPO = 
                saxoStatisticService2.querySaxoReconBalanceList(saxoReconBalCashPO);
        
        List<SaxoReconBalanceResDTO> lSaxoReconBalanceResDTO = BeanMapperUtils.mapList(lSaxoReconBalCashPO,SaxoReconBalanceResDTO.class);
        return RpcMessage.success(lSaxoReconBalanceResDTO);
    }
    
    @Override
    public RpcMessage<List<SaxoOpenPositionResDTO>> saxoOpenPositionRemoteExport(SaxoOpenPositionReqDTO saxoOpenPositionReqDTO) {

        SaxoOpenPositionStockPO saxoOpenPositionStockPO = new SaxoOpenPositionStockPO();
        saxoOpenPositionStockPO.setStartDate(saxoOpenPositionReqDTO.getStartCreateTime());
        saxoOpenPositionStockPO.setEndDate(saxoOpenPositionReqDTO.getEndCreateTime());
        List<SaxoOpenPositionStockPO> lSaxoOpenPositionStockPO = 
                saxoOpenPosStkService.querySaxoOpenPositionList(saxoOpenPositionStockPO);
        
        List<SaxoOpenPositionResDTO> lSaxoOpenPositionResDTO = BeanMapperUtils.mapList(lSaxoOpenPositionStockPO,SaxoOpenPositionResDTO.class);
        return RpcMessage.success(lSaxoOpenPositionResDTO);
    }
    
    @Override
    public RpcMessage<List<SaxoShareOpenPositionResDTO>> saxoShareOpenPositionRemoteExport(SaxoShareOpenPositionReqDTO saxoShareOpenPositionReqDTO) {

        SaxoShareOpenPositionPO saxoShareOpenPositionPO = new SaxoShareOpenPositionPO();
        saxoShareOpenPositionPO.setStartDate(saxoShareOpenPositionReqDTO.getStartCreateTime());
        saxoShareOpenPositionPO.setEndDate(saxoShareOpenPositionReqDTO.getEndCreateTime());
        List<SaxoShareOpenPositionPO> lSaxoShareOpenPositionPO = 
                saxoShareOpenPositionService.querySaxoShareOpenPositionList(saxoShareOpenPositionPO);
        
        List<SaxoShareOpenPositionResDTO> lSaxoShareOpenPositionResDTO = BeanMapperUtils.mapList(lSaxoShareOpenPositionPO,SaxoShareOpenPositionResDTO.class);
        return RpcMessage.success(lSaxoShareOpenPositionResDTO);
    }
}
