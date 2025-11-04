/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.controller;

import com.pivot.aham.api.server.remoteservice.NameAliasRemoteService;
import com.pivot.aham.api.web.in.vo.NameAliasManageProcessReqVo;
import com.pivot.aham.api.web.in.vo.NameAliasManageProcessResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.BankNameAliasReqDTO;
import com.pivot.aham.api.server.dto.BankNameAliasResDTO;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.NameAliasEnum;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author WooiTatt
 */
@CrossOrigin("*")
@Slf4j
@RestController
//@RequestMapping(value="/api/v1/in", headers = "Content-Type= multipart/form-data", method = RequestMethod.POST)
@RequestMapping(value="/api/v1/in")
@Api(value = "CustomerService - NameAlias ")
public class NameAliasManageController {
    
    @Resource
    private NameAliasRemoteService nameAliasRemoteService;
    
    @ApiOperation(value = "Retrieve unmerge name (Page)")
    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresPermissions("in:cs:*")
    public Message<NameAliasManageProcessResVo>  nameAliasApprove(@RequestParam("file1") MultipartFile file1, @RequestParam("file") MultipartFile file2, @RequestParam("hidden") String rechargeId) throws IOException {
        
       BankNameAliasReqDTO oBankNameAliasReqDTO = new BankNameAliasReqDTO();
       if(file1 != null){
           String numberOfFile ="1";
           saveUploadFile(file1,rechargeId,numberOfFile);
           oBankNameAliasReqDTO.setFileName1(rechargeId +"_"+numberOfFile+"_"+file1.getOriginalFilename());
       }
       
       if(file2 != null){
           String numberOfFile ="2";
           saveUploadFile(file2,rechargeId,numberOfFile);
            oBankNameAliasReqDTO.setFileName2(rechargeId +"_"+numberOfFile+"_"+file2.getOriginalFilename());
       }
       oBankNameAliasReqDTO.setRechargeId(rechargeId);
       oBankNameAliasReqDTO.setStatus(NameAliasEnum.APPROVE);
       nameAliasRemoteService.approvedNameAlias(oBankNameAliasReqDTO);
       
       return Message.success("Successfully");
    }
    
    
    
    @ApiOperation(value = "角色列表")
    @RequiresPermissions("in:cs:*")
    @PostMapping(value = "/namealias/list")
    public Message<Page<NameAliasManageProcessResVo>> query(@RequestBody NameAliasManageProcessReqVo nameAliasManageProcessReqVo) {
        
        Page<NameAliasManageProcessResVo> pagination = new Page<>();
        List<NameAliasManageProcessResVo> lNameAliasManageProcessResVo = Lists.newArrayList();

        BankNameAliasReqDTO bankNameAliasReqDTO = new BankNameAliasReqDTO();
        bankNameAliasReqDTO.setPageNo(nameAliasManageProcessReqVo.getPageNo());
        bankNameAliasReqDTO.setPageSize(nameAliasManageProcessReqVo.getPageSize());

        RpcMessage<Page<BankNameAliasResDTO>> bankNameAliasPageRPC
                =  nameAliasRemoteService.getBankNameAliasPage(bankNameAliasReqDTO);

        if(bankNameAliasPageRPC.isSuccess()){
            Page<BankNameAliasResDTO> bankNameAliasPage = bankNameAliasPageRPC.getContent();
            pagination = BeanMapperUtils.map(bankNameAliasPage,pagination.getClass());

            List<BankNameAliasResDTO> bankNameAliasResList = bankNameAliasPage.getRecords();
            for(BankNameAliasResDTO bankNameAliasResDTO:bankNameAliasResList){
                NameAliasManageProcessResVo nameAliasManageProcessResVo = new NameAliasManageProcessResVo();
                nameAliasManageProcessResVo.setRechargeId(bankNameAliasResDTO.getRechargeId());
                nameAliasManageProcessResVo.setBankClientName(bankNameAliasResDTO.getBankClientName());
                nameAliasManageProcessResVo.setClientId(bankNameAliasResDTO.getClientId());
                nameAliasManageProcessResVo.setBankClientName(bankNameAliasResDTO.getBankClientName());
                nameAliasManageProcessResVo.setSysClientName(bankNameAliasResDTO.getSysClientName());
                nameAliasManageProcessResVo.setVirtualAccountNo(bankNameAliasResDTO.getVirtualAccountNo());
                nameAliasManageProcessResVo.setStatus(bankNameAliasResDTO.getStatus().getDesc());
                nameAliasManageProcessResVo.setCreateTime(bankNameAliasResDTO.getCreateTime());
                lNameAliasManageProcessResVo.add(nameAliasManageProcessResVo);
            }
        }

        pagination.setRecords(lNameAliasManageProcessResVo);

        return Message.success(pagination);
    }
    
    @ApiOperation(value = "角色详情")
    @RequiresPermissions("in:cs:*")
    @PostMapping(value = "/namealias/clientinfo")
    
    public Message<NameAliasManageProcessResVo> getClientInfo(@RequestBody NameAliasManageProcessReqVo nameAliasManageProcessReqVo) {
        
        RpcMessage<BankNameAliasResDTO> oBankNameAliasResDTO = nameAliasRemoteService.queryClientInfo(nameAliasManageProcessReqVo.getRechargeId());
        
        NameAliasManageProcessResVo nameAliasManageProcessResVo = new NameAliasManageProcessResVo();
        if(oBankNameAliasResDTO.isSuccess()){
            BankNameAliasResDTO bankNameAliasResDTO = oBankNameAliasResDTO.getContent();
            nameAliasManageProcessResVo.setSysClientName(bankNameAliasResDTO.getSysClientName());
            nameAliasManageProcessResVo.setClientId(bankNameAliasResDTO.getClientId());
            nameAliasManageProcessResVo.setVirtualAccountNo(bankNameAliasResDTO.getVirtualAccountNo());
            nameAliasManageProcessResVo.setRechargeId(bankNameAliasResDTO.getRechargeId());
        
        }

        return Message.success(nameAliasManageProcessResVo);
    }
    
    @ApiOperation(value = "Retrieve unmerge name (Page)")
    @PostMapping(value = "/namealias/reject")
    @RequiresPermissions("in:cs:*")
    public Message<NameAliasManageProcessResVo> rejectedNameAlias(@RequestBody NameAliasManageProcessReqVo nameAliasManageProcessReqVo) {

        if(!nameAliasManageProcessReqVo.getRechargeId().equalsIgnoreCase("")){
            BankNameAliasReqDTO bankNameAliasReqDTO = new BankNameAliasReqDTO();
            bankNameAliasReqDTO.setRechargeId(nameAliasManageProcessReqVo.getRechargeId());
            bankNameAliasReqDTO.setReasonRejection(nameAliasManageProcessReqVo.getReasonRejection());
            nameAliasRemoteService.updateRejection(bankNameAliasReqDTO); 
        }

        return Message.success(null);
    }
    
    public void saveUploadFile(MultipartFile file, String rechargeId, String numberOfFile){
        File fileOne = new File(PropertiesUtil.getString("NAMEALIAS_LOCATION")+rechargeId+"_"+numberOfFile+"_"+file.getOriginalFilename());
            try {
                    fileOne.createNewFile();
                    FileOutputStream fos;
                    fos = new FileOutputStream(fileOne);
                    fos.write(file.getBytes());
                    fos.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(NameAliasManageController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(NameAliasManageController.class.getName()).log(Level.SEVERE, null, ex);
                }
    }

}
