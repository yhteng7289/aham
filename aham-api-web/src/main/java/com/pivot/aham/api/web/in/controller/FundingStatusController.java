package com.pivot.aham.api.web.in.controller;

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
import com.pivot.aham.api.server.dto.req.FundingStatusReqDTO;
import com.pivot.aham.api.server.dto.res.FundingStatusResDTO;
import com.pivot.aham.api.web.in.vo.FundingStatusReqVo;
import com.pivot.aham.api.web.in.vo.FundingStatusResVo;
import com.pivot.aham.api.server.remoteservice.FundingStatusRemoteService;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;

import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Resource;

/**
 *
 * @author bjoon
 */
@Slf4j
@RestController
//@RequestMapping(value="/api/v1/in", headers = "Content-Type= multipart/form-data", method = RequestMethod.POST)
@RequestMapping(value="/api/v1/in")
@Api(value = "CustomerService - FundingStatus ")
public class FundingStatusController {

    @Resource
    private FundingStatusRemoteService fundingStatusRemoteService;
    
    @ApiOperation(value = "Retrieve Funding Details")
    @RequiresPermissions("in:cs:*")
    @PostMapping(value = "/fundingStatus")
    public Message<Object> pageListing(@RequestBody FundingStatusReqVo fundingStatusReqVo) {
        FundingStatusReqDTO fundingStatusReqDTO = new FundingStatusReqDTO();
        fundingStatusReqDTO.setStartCreateTime(fundingStatusReqVo.getStartCreateTime());
        fundingStatusReqDTO.setEndCreateTime(fundingStatusReqVo.getEndCreateTime());
        fundingStatusReqDTO.setPageNo(fundingStatusReqVo.getPageNo());
        fundingStatusReqDTO.setPageSize(fundingStatusReqVo.getPageSize());
        
        if(!fundingStatusReqVo.getClientId().equalsIgnoreCase("")){
            fundingStatusReqDTO.setClientId(fundingStatusReqVo.getClientId());
        }
        
        RpcMessage<Page<FundingStatusResDTO>> rpcMessage = fundingStatusRemoteService.getFundingStatusPage(fundingStatusReqDTO);
        if (rpcMessage.isSuccess()) {
            Page<FundingStatusResVo> pagination = new Page<>();
            List<FundingStatusResVo> fundingStatusResVoList = Lists.newArrayList();
            Page<FundingStatusResDTO> fundingStatusPage = rpcMessage.getContent();
            pagination = BeanMapperUtils.map(fundingStatusPage,pagination.getClass());
            
            List<FundingStatusResDTO> fundingStatusRes = fundingStatusPage.getRecords();
            for(FundingStatusResDTO fundingStatusResDTO:fundingStatusRes) {
                    FundingStatusResVo fundingStatusResVo = new FundingStatusResVo();
                    fundingStatusResVo.setClientId(fundingStatusResDTO.getClientId());
                    fundingStatusResVo.setGoalId(fundingStatusResDTO.getGoalId());
                    fundingStatusResVo.setApplyAmountInSgd(fundingStatusResDTO.getApplyAmountInSgd());
                    fundingStatusResVo.setConfirmAmountInSgd(fundingStatusResDTO.getConfirmAmountInSgd());
                    fundingStatusResVo.setApplyAmountInUsd(fundingStatusResDTO.getApplyAmountInUsd());
                    
                    /*if(fundingStatusResVo.getGoalId().startsWith("GYC")) {
                    	fundingStatusResVo.setConfirmInSgd(fundingStatusResVo.getApplyAmountInUsd());
                    	fundingStatusResVo.setApplyAmountInUsd(BigDecimal.ZERO);
                    }else {
                    	fundingStatusResVo.setConfirmInSgd(BigDecimal.ZERO);
                    }*/
                    
                    if (fundingStatusResDTO.getOperateTypeEnum() != null &&
                            (fundingStatusResDTO.getOperateTypeEnum().equals(OperateTypeEnum.RECHARGE) || fundingStatusResDTO.getOperateTypeEnum() == OperateTypeEnum.RECHARGE)) {
//                    fundingStatusResVo.setStatus(String.valueOf(UserRechargeStatusEnum.forValue(Integer.parseInt(fundingStatusResDTO.getStatus()))));
                        /*if (fundingStatusResDTO.getStatus().equals("0") || fundingStatusResDTO.getStatus().equals("1") || fundingStatusResDTO.getStatus().equals("5"))
                            fundingStatusResVo.setStatus("Fund In Progress");
                        else if (fundingStatusResDTO.getStatus().equals("2"))
                            fundingStatusResVo.setStatus("Inter-Account Transfer");
                        else if (fundingStatusResDTO.getStatus().equals("3") && fundingStatusResDTO.getStatus2().equals("1"))
                            fundingStatusResVo.setStatus("In Trading");
                        else if (fundingStatusResDTO.getStatus().equals("3") && (fundingStatusResDTO.getStatus2().equals("2") || fundingStatusResDTO.getStatus2().equals("82")))
                            fundingStatusResVo.setStatus("Trading Completed");
                        else if (fundingStatusResDTO.getStatus().equals("4"))
                            fundingStatusResVo.setStatus("Failed");*/
                        if(fundingStatusResDTO.getStatus().equals("0")){
                            fundingStatusResVo.setStatus("Fund In Progress");
                        }else if(fundingStatusResDTO.getStatus().equals("1")){
                            fundingStatusResVo.setStatus("Trading in Progress");
                        }else if(fundingStatusResDTO.getStatus().equals("4")){
                            fundingStatusResVo.setStatus("Trade Order Confirmed");
                        }else if(fundingStatusResDTO.getStatus().equals("2")){
                            fundingStatusResVo.setStatus("Trade Completed");
                        }

                        fundingStatusResVo.setOperateType(OperateTypeEnum.RECHARGE);
                    } else if (fundingStatusResDTO.getOperateTypeEnum() != null &&
                            (fundingStatusResDTO.getOperateTypeEnum().equals(OperateTypeEnum.WITHDRAW) || fundingStatusResDTO.getOperateTypeEnum() == OperateTypeEnum.WITHDRAW)) {
//                    fundingStatusResVo.setStatus(String.valueOf(RedeemApplyStatusEnum.forValue(Integer.parseInt(fundingStatusResDTO.getStatus()))));
                        /*if (fundingStatusResDTO.getStatus().equals("0") && (fundingStatusResDTO.getStatus2().equals("0") || fundingStatusResDTO.getStatus2().equalsIgnoreCase("") || fundingStatusResDTO.getStatus2().equalsIgnoreCase("80") || fundingStatusResDTO.getStatus2().equalsIgnoreCase("81")))
                            fundingStatusResVo.setStatus("Withdraw In Progress");
                        if (fundingStatusResDTO.getStatus().equals("0") && fundingStatusResDTO.getStatus2().equals("1"))
                            fundingStatusResVo.setStatus("In Trading");
                        else if (fundingStatusResDTO.getStatus().equals("0") && (fundingStatusResDTO.getStatus2().equals("2") || fundingStatusResDTO.getStatus2().equals("82")))
                            fundingStatusResVo.setStatus("Trading Completed");
                        else if (fundingStatusResDTO.getStatus().equals("1"))
                            fundingStatusResVo.setStatus("Saxo payout UOB");
                        else if (fundingStatusResDTO.getStatus().equals("2"))
                            fundingStatusResVo.setStatus("Failed");*/
                        if(fundingStatusResDTO.getStatus().equals("0")){
                            fundingStatusResVo.setStatus("Withdrawal In Progress");
                        }else if(fundingStatusResDTO.getStatus().equals("1")){
                            fundingStatusResVo.setStatus("Trading in Progress");
                        }else if(fundingStatusResDTO.getStatus().equals("4")){
                            fundingStatusResVo.setStatus("Trade Order Confirmed");
                        }else if(fundingStatusResDTO.getStatus().equals("2")){
                            fundingStatusResVo.setStatus("Trade Completed");
                        }
                        fundingStatusResVo.setOperateType(OperateTypeEnum.WITHDRAW);
                    }

                    fundingStatusResVo.setCreateTime(fundingStatusResDTO.getCreateTime());

                   if (fundingStatusReqVo.getOperationType().equalsIgnoreCase("") || fundingStatusReqVo.getOperationType().equals(fundingStatusResVo.getOperateType().toString())) {
                       fundingStatusResVoList.add(fundingStatusResVo);
                   }


            }

            pagination.setRecords(fundingStatusResVoList);
            return Message.success(pagination);
        } else {
            log.info("===============isNotSuccess ErrMsg====================" +rpcMessage.getErrMsg());
            return Message.success();
        }
    }

}
