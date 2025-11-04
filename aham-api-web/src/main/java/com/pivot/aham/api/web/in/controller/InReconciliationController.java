/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.controller;

import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pivot.aham.api.server.dto.SaxoReconBalanceReqDTO;
import com.pivot.aham.api.server.dto.SaxoReconBalanceResDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoStatisticRemoteService;
import javax.annotation.Resource;

import com.alibaba.dubbo.common.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.AccountAssetResDTO;
import com.pivot.aham.api.server.dto.SaxoOpenPositionReqDTO;
import com.pivot.aham.api.server.dto.SaxoOpenPositionResDTO;
import com.pivot.aham.api.server.dto.SaxoShareOpenPositionReqDTO;
import com.pivot.aham.api.server.dto.SaxoShareOpenPositionResDTO;
import com.pivot.aham.api.server.dto.SaxoShareTradeReqDTO;
import com.pivot.aham.api.server.dto.SaxoShareTradeResDTO;
import com.pivot.aham.api.web.in.vo.AhamReconReqVo;
import com.pivot.aham.api.web.in.vo.SaxoOpenPositionReqVo;
import com.pivot.aham.api.web.in.vo.SaxoOpenPositionResVo;
import com.pivot.aham.api.web.in.vo.SaxoReconBalanceReqVo;
import com.pivot.aham.api.web.in.vo.SaxoReconBalanceResVo;
import com.pivot.aham.api.web.in.vo.SaxoShareOpenPositionReqVo;
import com.pivot.aham.api.web.in.vo.SaxoShareOpenPositionResVo;
import com.pivot.aham.api.web.in.vo.SaxoShareTradeReqVo;
import com.pivot.aham.api.web.in.vo.SaxoShareTradeResVo;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.util.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api/v1/in")
@Api(value = "账户资产管理", description = "账户资产管理接口")
public class InReconciliationController {
    
	private final Logger log = LoggerFactory.getLogger(InReconciliationController.class);
	
    @Resource
    private SaxoStatisticRemoteService saxoStatisticRemoteService;
    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;
    
    @ApiOperation(value = "获取account的Etf列表")
    @PostMapping("/saxoReconciliation/saxoReconBalance")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<Page<SaxoReconBalanceResDTO>> saxoReconBalance(@RequestBody SaxoReconBalanceReqVo saxoReconBalanceReqVo) {
        
        SaxoReconBalanceReqDTO saxoReconBalReqDTO = new SaxoReconBalanceReqDTO();
        saxoReconBalReqDTO.setPageNo(saxoReconBalanceReqVo.getPageNo());
        saxoReconBalReqDTO.setPageSize(saxoReconBalanceReqVo.getPageSize());
        saxoReconBalReqDTO.setStartCreateTime(saxoReconBalanceReqVo.getStartCreateTime());
        saxoReconBalReqDTO.setEndCreateTime(saxoReconBalanceReqVo.getEndCreateTime());
        
        RpcMessage<Page<SaxoReconBalanceResDTO>> rpcSaxoReconBal
                = saxoStatisticRemoteService.saxoReconBalance(saxoReconBalReqDTO);
        
        return Message.success(rpcSaxoReconBal.getContent());
    }
    
    @ApiOperation(value = "获取account的Etf列表")
    @PostMapping("/saxoReconciliation/saxoOpenPositionStock")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<Page<SaxoOpenPositionResDTO>> saxoOpenPositionStock(@RequestBody SaxoOpenPositionReqVo saxoOpenPositinReqVo) {
        
        SaxoOpenPositionReqDTO saxoOpenPositionReqDTO = new SaxoOpenPositionReqDTO();
        saxoOpenPositionReqDTO.setPageNo(saxoOpenPositinReqVo.getPageNo());
        saxoOpenPositionReqDTO.setPageSize(saxoOpenPositinReqVo.getPageSize());
        saxoOpenPositionReqDTO.setStartCreateTime(saxoOpenPositinReqVo.getStartCreateTime());
        saxoOpenPositionReqDTO.setEndCreateTime(saxoOpenPositinReqVo.getEndCreateTime());
        
        RpcMessage<Page<SaxoOpenPositionResDTO>> rpcSaxoOpenPositionRes
                = saxoStatisticRemoteService.saxoOpenPositionStock(saxoOpenPositionReqDTO);
        
        System.out.println("saxoReconBalance Success In" + rpcSaxoOpenPositionRes.getContent().getRecords().size());

        return Message.success(rpcSaxoOpenPositionRes.getContent());
    }
    
    @ApiOperation(value = "获取account的Etf列表")
    @PostMapping("/saxoReconciliation/saxoShareTrade")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<Page<SaxoShareTradeResDTO>> saxoShareTrade(@RequestBody SaxoShareTradeReqVo saxoShareTradeReqVo) {
        
        SaxoShareTradeReqDTO saxoShareTradeReqDTO = new SaxoShareTradeReqDTO();
        saxoShareTradeReqDTO.setPageNo(saxoShareTradeReqVo.getPageNo());
        saxoShareTradeReqDTO.setPageSize(saxoShareTradeReqVo.getPageSize());
        saxoShareTradeReqDTO.setStartCreateTime(saxoShareTradeReqVo.getStartCreateTime());
        saxoShareTradeReqDTO.setEndCreateTime(saxoShareTradeReqVo.getEndCreateTime());
        
        RpcMessage<Page<SaxoShareTradeResDTO>> rpcSaxoShareTradeRes
                = saxoStatisticRemoteService.saxoShareTrade(saxoShareTradeReqDTO);

        return Message.success(rpcSaxoShareTradeRes.getContent());
    }
    
    @ApiOperation(value = "获取account的Etf列表")
    @PostMapping("/saxoReconciliation/saxoShareOpenPosition")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<Page<SaxoShareOpenPositionResDTO>> saxoShareOpenPosition(@RequestBody SaxoShareOpenPositionReqVo saxoShareOpenPositionReqVo) {
        
        SaxoShareOpenPositionReqDTO saxoShareOpenPositionReqDTO = new SaxoShareOpenPositionReqDTO();
        saxoShareOpenPositionReqDTO.setPageNo(saxoShareOpenPositionReqVo.getPageNo());
        saxoShareOpenPositionReqDTO.setPageSize(saxoShareOpenPositionReqVo.getPageSize());
        saxoShareOpenPositionReqDTO.setStartCreateTime(saxoShareOpenPositionReqVo.getStartCreateTime());
        saxoShareOpenPositionReqDTO.setEndCreateTime(saxoShareOpenPositionReqVo.getEndCreateTime());
      
        RpcMessage<Page<SaxoShareOpenPositionResDTO>> rpcSaxoShareOpenPositionRes
                = saxoStatisticRemoteService.saxoShareOpenPosition(saxoShareOpenPositionReqDTO);

        return Message.success(rpcSaxoShareOpenPositionRes.getContent());
    }
    
    @ApiOperation(value = "导出某个client的transction")
    @GetMapping("/saxoReconciliation/saxoReconBalanceExport")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<List<SaxoReconBalanceResVo>> exportSaxoReconBalance(
            SaxoReconBalanceReqVo saxoReconBalanceReqVo,
            HttpServletResponse response) {
        
      
         List<SaxoReconBalanceResVo> saxoReconBalanceResVoList = Lists.newArrayList();
         
         SaxoReconBalanceReqDTO saxoReconBalanceReqDTO = new SaxoReconBalanceReqDTO();
         saxoReconBalanceReqDTO.setCreateTime(saxoReconBalanceReqVo.getStartCreateTime());
         saxoReconBalanceReqDTO.setEndCreateTime(saxoReconBalanceReqVo.getEndCreateTime());
         RpcMessage<List<SaxoReconBalanceResDTO>> rpcMessagePageOrder
                = saxoStatisticRemoteService.saxoReconBalanceRemoteExport(saxoReconBalanceReqDTO);
        
         if (rpcMessagePageOrder.isSuccess()) {
            
            List<SaxoReconBalanceResDTO> saxoReconBalanceResDTOList = rpcMessagePageOrder.getContent();
            for (SaxoReconBalanceResDTO saxoReconBalanceResDTO : saxoReconBalanceResDTOList) {
                SaxoReconBalanceResVo saxoReconBalanceResVo = new SaxoReconBalanceResVo();
                saxoReconBalanceResVo.setId(saxoReconBalanceResDTO.getId());
                saxoReconBalanceResVo.setSaxoCash(saxoReconBalanceResDTO.getSaxoCash());
                saxoReconBalanceResVo.setDasCash(saxoReconBalanceResDTO.getDasCash());
                saxoReconBalanceResVo.setDiffCash(saxoReconBalanceResDTO.getDiffCash());
                saxoReconBalanceResVo.setStatusDes(saxoReconBalanceResDTO.getStatusDes());
                saxoReconBalanceResVo.setFileName(saxoReconBalanceResDTO.getFileName());
                saxoReconBalanceResVo.setTransNumber(saxoReconBalanceResDTO.getTransNumber());
                saxoReconBalanceResVo.setCompareTime(saxoReconBalanceResDTO.getCompareTime());
                saxoReconBalanceResVoList.add(saxoReconBalanceResVo);
            }
         }
             
        ExportExcel exportExcel = new ExportExcel(null, SaxoReconBalanceResVo.class);
        exportExcel.setDataList(saxoReconBalanceResVoList);

        String dateStr = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String fileName = "saxoReconBalance" + dateStr + ".xlsx";
        try {
            exportExcel.write(response, fileName);
            exportExcel.dispose();
        } catch (Exception e) {
            return Message.error("下载文件失败" + e.getMessage());
        }
        return null;
    }
    
    
     @ApiOperation(value = "导出某个client的transction")
    @GetMapping("/saxoReconciliation/saxoOpenPositionStockExport")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<List<SaxoOpenPositionResVo>> exportOpenPositionStock(
            SaxoOpenPositionReqVo saxoOpenPositionReqVo,
            HttpServletResponse response) {
        
         List<SaxoOpenPositionResVo> saxoOpenPositionResVoList = Lists.newArrayList();
         
         SaxoOpenPositionReqDTO saxoOpenPositionReqDTO = new SaxoOpenPositionReqDTO();
         saxoOpenPositionReqDTO.setCreateTime(saxoOpenPositionReqVo.getStartCreateTime());
         saxoOpenPositionReqDTO.setEndCreateTime(saxoOpenPositionReqVo.getEndCreateTime());
         RpcMessage<List<SaxoOpenPositionResDTO>> rpcMessagePageOrder
                = saxoStatisticRemoteService.saxoOpenPositionRemoteExport(saxoOpenPositionReqDTO);
        
         if (rpcMessagePageOrder.isSuccess()) {
            
            List<SaxoOpenPositionResDTO> saxoOpenPositionResDTOList = rpcMessagePageOrder.getContent();
            for (SaxoOpenPositionResDTO saxoOpenPositionResDTO : saxoOpenPositionResDTOList) {
                SaxoOpenPositionResVo saxoOpenPositionResVo = new SaxoOpenPositionResVo();
                saxoOpenPositionResVo.setId(saxoOpenPositionResDTO.getId());
                saxoOpenPositionResVo.setSaxoHoldMoney(saxoOpenPositionResDTO.getSaxoHoldMoney());
                saxoOpenPositionResVo.setDasHoldMoney(saxoOpenPositionResDTO.getDasHoldMoney());
                saxoOpenPositionResVo.setStatusDes(saxoOpenPositionResDTO.getStatusDes());
                saxoOpenPositionResVo.setFileName(saxoOpenPositionResDTO.getFileName());
                saxoOpenPositionResVo.setTransNumber(saxoOpenPositionResDTO.getTransNumber());
                saxoOpenPositionResVo.setCompareTime(saxoOpenPositionResDTO.getCompareTime());
                saxoOpenPositionResVoList.add(saxoOpenPositionResVo);
            }
         }
            
            
        ExportExcel exportExcel = new ExportExcel(null, SaxoOpenPositionResVo.class);
        exportExcel.setDataList(saxoOpenPositionResVoList);

        String dateStr = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String fileName = "saxoOpenPosition" + dateStr + ".xlsx";
        try {
            exportExcel.write(response, fileName);
            exportExcel.dispose();
        } catch (Exception e) {
            return Message.error("下载文件失败" + e.getMessage());
        }
        return null;
    }

    
    @ApiOperation(value = "导出某个client的transction")
    @GetMapping("/saxoReconciliation/saxoShareTradeExport")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<List<SaxoShareTradeResVo>> exportShareTrade(
            SaxoShareTradeReqVo saxoShareTradeReqVo,
            HttpServletResponse response) {
        
         List<SaxoShareTradeResVo> saxoShareTradeResVoList = Lists.newArrayList();
         
         SaxoShareTradeReqDTO saxoShareTradeReqDTO = new SaxoShareTradeReqDTO();
         saxoShareTradeReqDTO.setCreateTime(saxoShareTradeReqVo.getStartCreateTime());
         saxoShareTradeReqDTO.setEndCreateTime(saxoShareTradeReqVo.getEndCreateTime());
         RpcMessage<List<SaxoShareTradeResDTO>> rpcMessagePageOrder
                = saxoStatisticRemoteService.saxoShareTradeRemoteExport(saxoShareTradeReqDTO);
        
         if (rpcMessagePageOrder.isSuccess()) {
            
            List<SaxoShareTradeResDTO> saxoShareTradeResDTOList = rpcMessagePageOrder.getContent();
            for (SaxoShareTradeResDTO saxoShareTradeResDTO : saxoShareTradeResDTOList) {
                SaxoShareTradeResVo saxoShareTradeResVo = new SaxoShareTradeResVo();
                saxoShareTradeResVo.setId(saxoShareTradeResDTO.getId());
                saxoShareTradeResVo.setProductCode(saxoShareTradeResDTO.getProductCode());
                saxoShareTradeResVo.setSaxoTradeShare(saxoShareTradeResDTO.getSaxoTradeShare());
                saxoShareTradeResVo.setDasTradeShare(saxoShareTradeResDTO.getDasTradeShare());
                saxoShareTradeResVo.setSaxoCommission(saxoShareTradeResDTO.getSaxoCommission());
                saxoShareTradeResVo.setDasCommission(saxoShareTradeResDTO.getDasCommission());
                saxoShareTradeResVo.setStatusDes(saxoShareTradeResDTO.getStatusDes());
                saxoShareTradeResVo.setFileName(saxoShareTradeResDTO.getFileName());
                saxoShareTradeResVo.setOrderNumber(saxoShareTradeResDTO.getOrderNumber());
                saxoShareTradeResVo.setTransNumber(saxoShareTradeResDTO.getTransNumber());
                saxoShareTradeResVo.setCompareTime(saxoShareTradeResDTO.getCompareTime());
                saxoShareTradeResVoList.add(saxoShareTradeResVo); 
            }
         }
                 
        ExportExcel exportExcel = new ExportExcel(null, SaxoShareTradeResVo.class);
        exportExcel.setDataList(saxoShareTradeResVoList);

        String dateStr = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String fileName = "saxoShareTrade" + dateStr + ".xlsx";
        try {
            exportExcel.write(response, fileName);
            exportExcel.dispose();
        } catch (Exception e) {
            return Message.error("下载文件失败" + e.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "导出某个client的transction")
    @GetMapping("/saxoReconciliation/saxoShareOpenPositionExport")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<List<SaxoShareOpenPositionResVo>> exportShareOpenPosition(
            SaxoShareOpenPositionReqVo saxoShareOpenPositionReqVo,
            HttpServletResponse response) {
        
         List<SaxoShareOpenPositionResVo> saxoShareOpenPositionResVoList = Lists.newArrayList();
         
         SaxoShareOpenPositionReqDTO saxoShareOpenPositionReqDTO = new SaxoShareOpenPositionReqDTO();
         saxoShareOpenPositionReqDTO.setCreateTime(saxoShareOpenPositionReqVo.getStartCreateTime());
         saxoShareOpenPositionReqDTO.setEndCreateTime(saxoShareOpenPositionReqVo.getEndCreateTime());
         RpcMessage<List<SaxoShareOpenPositionResDTO>> rpcMessagePageOrder
                = saxoStatisticRemoteService.saxoShareOpenPositionRemoteExport(saxoShareOpenPositionReqDTO);
        
         if (rpcMessagePageOrder.isSuccess()) {

            List<SaxoShareOpenPositionResDTO> saxoShareOpenPositionResDTOList = rpcMessagePageOrder.getContent();
            for (SaxoShareOpenPositionResDTO saxoShareOpenPositionResDTO : saxoShareOpenPositionResDTOList) {
                SaxoShareOpenPositionResVo saxoShareOpenPositionResVo = new SaxoShareOpenPositionResVo();
                saxoShareOpenPositionResVo.setId(saxoShareOpenPositionResDTO.getId());
                saxoShareOpenPositionResVo.setProductCode(saxoShareOpenPositionResDTO.getProductCode());
                saxoShareOpenPositionResVo.setSaxoHoldShare(saxoShareOpenPositionResDTO.getSaxoHoldShare());
                saxoShareOpenPositionResVo.setDasHoldShare(saxoShareOpenPositionResDTO.getDasHoldShare());
                saxoShareOpenPositionResVo.setSaxoHoldAmount(saxoShareOpenPositionResDTO.getSaxoHoldAmount());
                saxoShareOpenPositionResVo.setDasHoldAmount(saxoShareOpenPositionResDTO.getDasHoldAmount());
                saxoShareOpenPositionResVo.setStatusDes(saxoShareOpenPositionResDTO.getStatusDes());
                saxoShareOpenPositionResVo.setFileName(saxoShareOpenPositionResDTO.getFileName());
                saxoShareOpenPositionResVo.setTransNumber(saxoShareOpenPositionResDTO.getTransNumber());
                saxoShareOpenPositionResVo.setCompareTime(saxoShareOpenPositionResDTO.getCompareTime());
                saxoShareOpenPositionResVoList.add(saxoShareOpenPositionResVo);              
            }
         }
                    
        ExportExcel exportExcel = new ExportExcel(null, SaxoShareOpenPositionResVo.class);
        exportExcel.setDataList(saxoShareOpenPositionResVoList);

        String dateStr = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String fileName = "saxoShareOpenPositin" + dateStr + ".xlsx";
        try {
            exportExcel.write(response, fileName);
            exportExcel.dispose();
        } catch (Exception e) {
            return Message.error("下载文件失败" + e.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "Get DAS Unit")
    @PostMapping("/aham/recon/getDasUnit")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<String> getDasUnit() {
    	String result = "0.00";
    	RpcMessage<String> rpcMessage = assetServiceRemoteService.getDasUnit();
    	if(rpcMessage.isSuccess()) {
    		result = rpcMessage.getContent();
    	}
        return Message.success("Record found.", result);
    }
    
    @ApiOperation(value = "Add Aham Recon")
    @PostMapping("/aham/recon/addAhamRecon")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<String> addAhamRecon(@RequestBody AhamReconReqVo ahamReconReq) {
    	log.info("===ahamReconReq:{}",JSON.toJSONString(ahamReconReq.getOrderDetailReqList()));
        List<AccountAssetResDTO> listAccAssetResDTO = Lists.newArrayList();
        for(AhamReconReqVo ahamReconReqVo: ahamReconReq.getOrderDetailReqList()){
            AccountAssetResDTO accountAssetResDTO = new AccountAssetResDTO();
            accountAssetResDTO.setProductCode(ahamReconReqVo.getProdCode());
            accountAssetResDTO.setProductShare(ahamReconReqVo.getDasUnit());
            accountAssetResDTO.setReconShareUnit(ahamReconReqVo.getInputUnit());
            listAccAssetResDTO.add(accountAssetResDTO);
            
        }
    	/*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("dasUnit", ahamReconReq.getDasUnit()+"");
    	map.put("inputUnit", ahamReconReq.getInputUnit()+"");
    	map.put("dasTime", dateFormat.format(ahamReconReq.getDasTime()));*/
    	String result = "0.00";
    	RpcMessage<String> rpcMessage = assetServiceRemoteService.saveAhamRecon(listAccAssetResDTO);
    	if(rpcMessage.isSuccess()) {
    		result = rpcMessage.getContent();
    	}
        return Message.success("Process Done.", result);
    }
    
    @ApiOperation(value = "Find Aham Recon Page")
    @PostMapping("/aham/recon/findAhamReconPage")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<JSONArray> findAhamReconPage(@RequestBody AhamReconReqVo ahamReconReq) {
    	
    	Date startCreateTime = null;
    	Date endCreateTime = null;
    	
    	if(ahamReconReq.getStartCreateTime() != null) startCreateTime = ahamReconReq.getStartCreateTime();
    	if(ahamReconReq.getEndCreateTime() != null) endCreateTime = ahamReconReq.getEndCreateTime();
    	
    	JSONArray result = new JSONArray();
    	RpcMessage<JSONArray> rpcMessage = assetServiceRemoteService.findAhamReconPage(startCreateTime, endCreateTime);
    	if(rpcMessage.isSuccess()) {
    		result = rpcMessage.getContent();
    	}
        return Message.success(result);
    }
    
    @ApiOperation(value = "Get DAS Unit")
    @PostMapping("/aham/recon/getDasProdUnit")
    @RequiresPermissions("in:saxoRecon:read")
    public Message<List<AhamReconReqVo>> getDasProdUnit() {
        
        List<AhamReconReqVo> listAhamReconReq = Lists.newArrayList();
    	RpcMessage<List<AccountAssetResDTO>> rpcMessage = assetServiceRemoteService.getDasProdUnit();
    	if(rpcMessage.isSuccess()) {
            List<AccountAssetResDTO> listAccountAssetDTO = rpcMessage.getContent();
            for(AccountAssetResDTO accountAssetResDTO: listAccountAssetDTO){
                AhamReconReqVo ahamReconReqVo = new AhamReconReqVo();
                ahamReconReqVo.setDasUnit(accountAssetResDTO.getProductShare());
                ahamReconReqVo.setProdCode(accountAssetResDTO.getProductCode());
                listAhamReconReq.add(ahamReconReqVo);
            }
            
    	}
        return Message.success(listAhamReconReq);
    }
}
