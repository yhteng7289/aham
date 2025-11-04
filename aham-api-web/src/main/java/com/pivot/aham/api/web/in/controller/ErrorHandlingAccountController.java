/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.ErrorHandlingAccountReqDTO;
import com.pivot.aham.api.server.dto.res.ErrorHandlingAccountResDTO;
import com.pivot.aham.api.server.remoteservice.PivotErrorDetailRemoteService;
import com.pivot.aham.api.web.in.vo.ErrorHandlingAccountReqVo;
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
public class ErrorHandlingAccountController {

    @Resource
    private PivotErrorDetailRemoteService pivotErrorDetailRemoteService;

    @ApiOperation(value = "Retrieve Error Handling Account Total Amount (Header)")
    @PostMapping("/errorHandingAccount/money")
    @RequiresPermissions("in:pivotAccountAsset:*")
    public Message<BigDecimal> totalMoney() {
        RpcMessage<BigDecimal> totalMoneyRpcMessage = pivotErrorDetailRemoteService.getTotalMoney();
        if (totalMoneyRpcMessage.isSuccess()) {
            return Message.success(totalMoneyRpcMessage.getContent().setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            return Message.success();
        }
    }

    @ApiOperation(value = "Retrieve Error Handling Account (Page)")
    @PostMapping("/errorHandingAccount/page")
    @RequiresPermissions("in:pivotAccountAsset:*")
    public Message<Object> pageListing(@RequestBody ErrorHandlingAccountReqVo rrrorHandlingAccountReqVo) {
        ErrorHandlingAccountReqDTO errorHandlingAccountReqDTO = new ErrorHandlingAccountReqDTO();
        errorHandlingAccountReqDTO.setStartCreateTime(rrrorHandlingAccountReqVo.getStartCreateTime());
        errorHandlingAccountReqDTO.setEndCreateTime(rrrorHandlingAccountReqVo.getEndCreateTime());
        errorHandlingAccountReqDTO.setPageNo(rrrorHandlingAccountReqVo.getPageNo());
        errorHandlingAccountReqDTO.setPageSize(rrrorHandlingAccountReqVo.getPageSize());
        RpcMessage<Page<ErrorHandlingAccountResDTO>> rpcMessage = pivotErrorDetailRemoteService.getErrorHandlingPage(errorHandlingAccountReqDTO);
        if (rpcMessage.isSuccess()) {
            return Message.success(rpcMessage.getContent());
        } else {
            return Message.success();
        }
    }

}
