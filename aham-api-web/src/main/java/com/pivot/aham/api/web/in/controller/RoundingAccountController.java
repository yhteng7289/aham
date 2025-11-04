/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.RoundingAccountReqDTO;
import com.pivot.aham.api.server.dto.res.RoundingAccountResDTO;
import com.pivot.aham.api.server.remoteservice.PivotCharityDetailRemoteService;
import com.pivot.aham.api.web.in.vo.RoundingAccountReqVo;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import javax.annotation.Resource;

/**
 *
 * @author HP
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/in")
@Api(value = "Pivot Account - Error Handling Account Account")
public class RoundingAccountController {

    @Resource
    private PivotCharityDetailRemoteService pivotCharityDetailRemoteService;

    @ApiOperation(value = "Retrieve Rounding Account Total Amount (Header)")
    @PostMapping("/roundingAccount/money")
    @RequiresPermissions("in:pivotAccountAsset:*")
    public Message<BigDecimal> totalMoney() {
        RpcMessage<BigDecimal> totalMoneyRpcMessage = pivotCharityDetailRemoteService.getTotalMoney();
        if (totalMoneyRpcMessage.isSuccess()) {
            return Message.success(totalMoneyRpcMessage.getContent().setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            return Message.success();
        }
    }

    @ApiOperation(value = "Retrieve Rounding Account (Page)")
    @PostMapping("/roundingAccount/page")
    @RequiresPermissions("in:pivotAccountAsset:*")
    public Message<Object> pageListing(@RequestBody RoundingAccountReqVo roundingAccountReqVo) {
        RoundingAccountReqDTO roundingAccountReqDTO = new RoundingAccountReqDTO();
        roundingAccountReqDTO.setStartCreateTime(roundingAccountReqVo.getStartCreateTime());
        roundingAccountReqDTO.setEndCreateTime(roundingAccountReqVo.getEndCreateTime());
        roundingAccountReqDTO.setPageNo(roundingAccountReqVo.getPageNo());
        roundingAccountReqDTO.setPageSize(roundingAccountReqVo.getPageSize());
        RpcMessage<Page<RoundingAccountResDTO>> rpcMessage = pivotCharityDetailRemoteService.getRoundingAccountPage(roundingAccountReqDTO);
        if (rpcMessage.isSuccess()) {
            return Message.success(rpcMessage.getContent());
        } else {
            return Message.success();
        }
    }

}
