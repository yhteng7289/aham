package com.pivot.aham.api.web.in.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoResDTO;
import com.pivot.aham.api.server.dto.req.UserDividendReqDTO;
import com.pivot.aham.api.server.dto.res.UserDividendResDTO;
import com.pivot.aham.api.server.remoteservice.DividendRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.web.in.vo.DividendReqVo;
import com.pivot.aham.api.web.in.vo.DividendResVo;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/v1/in")
@Api(value = "Dividend Controller", description = "Dividend API")
public class InDividendController {
    
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    
    @Resource
    private DividendRemoteService dividendRemoteService;
    
    @ApiOperation(value = "List User Goal Information")
    @PostMapping("/dividend/dividendGoalClose")
    @RequiresPermissions("in:dividend:*")
    public Message<Page<DividendResVo>> userGoalInfoList(@RequestBody DividendReqVo dividendReqVo) {
        
        Page<DividendResVo> pagination = new Page<>();
        List<DividendResVo> userGoalInfoResVoList = Lists.newArrayList();
        
        UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
        userGoalInfoDTO.setPageNo(dividendReqVo.getPageNo());
        userGoalInfoDTO.setPageSize(dividendReqVo.getPageSize());
        userGoalInfoDTO.setDeleted("1");
        if(!dividendReqVo.getClientId().equalsIgnoreCase("")){
            userGoalInfoDTO.setClientId(dividendReqVo.getClientId());
        }
        
        RpcMessage<Page<UserGoalInfoResDTO>> userGoalInfoRpc
                =  userServiceRemoteService.getUserGoalInfoPage(userGoalInfoDTO);
        
        if(userGoalInfoRpc.isSuccess()){
            Page<UserGoalInfoResDTO> userGoalInfoPage = userGoalInfoRpc.getContent();
            pagination = BeanMapperUtils.map(userGoalInfoPage,pagination.getClass());
            
            List<UserGoalInfoResDTO> userGoalInfoRes = userGoalInfoPage.getRecords();
            for(UserGoalInfoResDTO userGoalInfoResDTO:userGoalInfoRes){
                DividendResVo dividendResVo = new DividendResVo();
                dividendResVo.setClientId(userGoalInfoResDTO.getClientId());
                dividendResVo.setGoalId(userGoalInfoResDTO.getGoalId());
                dividendResVo.setGoalName(userGoalInfoResDTO.getGoalName());
                dividendResVo.setPortfolioId(userGoalInfoResDTO.getPortfolioId());
                dividendResVo.setReferenceCode(userGoalInfoResDTO.getReferenceCode());
                userGoalInfoResVoList.add(dividendResVo);
            }
        }
        
        pagination.setRecords(userGoalInfoResVoList);
        return Message.success(pagination);
    }
    
    @ApiOperation(value = "List User Goal Information")
    @PostMapping("/dividend/userDividendDetails")
    @RequiresPermissions("in:dividend:*")
    public Message<Page<DividendResVo>> userDividendDetails(@RequestBody DividendReqVo dividendReqVo) {
        
        Page<DividendResVo> pagination = new Page<>();
        List<DividendResVo> dividendResVoList = Lists.newArrayList();
        
        UserDividendReqDTO userDividendReqDTO = new UserDividendReqDTO();
        userDividendReqDTO.setPageNo(dividendReqVo.getPageNo());
        userDividendReqDTO.setPageSize(dividendReqVo.getPageSize());
        userDividendReqDTO.setGoalId(dividendReqVo.getGoalId());
        
        RpcMessage<Page<UserDividendResDTO>> userDividendRPC
                =  dividendRemoteService.getUserDividendPage(userDividendReqDTO);
        
        if(userDividendRPC.isSuccess()){
            Page<UserDividendResDTO> userDividendPage = userDividendRPC.getContent();
            pagination = BeanMapperUtils.map(userDividendPage,pagination.getClass());
            
            List<UserDividendResDTO> userDividendRes = userDividendPage.getRecords();
            for(UserDividendResDTO userDividendResDTO:userDividendRes){
                DividendResVo dividendResVo = new DividendResVo();
                dividendResVo.setGoalId(userDividendResDTO.getGoalId());
                dividendResVo.setClientId(userDividendResDTO.getClientId());
                dividendResVo.setAccountId(userDividendResDTO.getAccountId());
                dividendResVo.setDividendDate(userDividendResDTO.getDividendDate());
                dividendResVo.setDividendAmount(userDividendResDTO.getDividendAmount());
                dividendResVo.setProductCode(userDividendResDTO.getProductCode());
                dividendResVoList.add(dividendResVo);
            }
        }
        
        pagination.setRecords(dividendResVoList);
        return Message.success(pagination);
    }

}
