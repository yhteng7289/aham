package com.pivot.aham.api.web.in.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.req.AccountBalanceHisRecordReqDTO;
import com.pivot.aham.api.server.dto.res.AccountBalanceHisRecordResDTO;
import com.pivot.aham.api.server.remoteservice.AccountReBalanceRemoteService;
import com.pivot.aham.api.web.in.vo.RebalancingReqVo;
import com.pivot.aham.api.web.in.vo.RebalancingResVo;
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
@Api(value = "Rebalancing Controller", description = "Rebalancing API")
public class InRebalancingController {

    @Resource
    private AccountReBalanceRemoteService accountReBalanceRemoteService;
    
    @ApiOperation(value = "List User Goal Information")
    @PostMapping("/rebalancing/rebalancingHisRecord")
    @RequiresPermissions("in:rebalancing:*")
    public Message<Page<RebalancingResVo>> rebalancingHisRecord(@RequestBody RebalancingReqVo rebalancingReqVo) {
        
        Page<RebalancingResVo> pagination = new Page<>();
        List<RebalancingResVo> rebalancingResVoList = Lists.newArrayList();
        
        AccountBalanceHisRecordReqDTO accountBalanceHisRecordReqDTO = new AccountBalanceHisRecordReqDTO();
        accountBalanceHisRecordReqDTO.setPageNo(rebalancingReqVo.getPageNo());
        accountBalanceHisRecordReqDTO.setPageSize(rebalancingReqVo.getPageSize());

        
        RpcMessage<Page<AccountBalanceHisRecordResDTO>> accBalHisRecRPC
                =  accountReBalanceRemoteService.getAccBalanceHisPage(accountBalanceHisRecordReqDTO);
        
        if(accBalHisRecRPC.isSuccess()){
            Page<AccountBalanceHisRecordResDTO> accBalanceHisPage = accBalHisRecRPC.getContent();
            pagination = BeanMapperUtils.map(accBalanceHisPage,pagination.getClass());
            
            List<AccountBalanceHisRecordResDTO> accountBalanceHisRecordRes = accBalanceHisPage.getRecords();
            for(AccountBalanceHisRecordResDTO accountBalanceHisRecordResDTO:accountBalanceHisRecordRes){
                RebalancingResVo rebalancingResVo = new RebalancingResVo();
                rebalancingResVo.setAccountId(accountBalanceHisRecordResDTO.getAccountId());
                rebalancingResVo.setBalId(accountBalanceHisRecordResDTO.getBalId());
                rebalancingResVo.setLastProductWeight(accountBalanceHisRecordResDTO.getLastProductWeight());
                rebalancingResVo.setPortfolioScore(accountBalanceHisRecordResDTO.getPortfolioScore());
                rebalancingResVo.setLastBalTime(accountBalanceHisRecordResDTO.getLastBalTime());
                rebalancingResVoList.add(rebalancingResVo);
            }
        }
        
        pagination.setRecords(rebalancingResVoList);
        return Message.success(pagination);
    }
    

}
