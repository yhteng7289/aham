///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.pivot.aham.api.web.in.controller;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// *
// * @author HP
// */
//@Slf4j
//@RestController
//@RequestMapping("/api/v1/in")
//@Api(value = "Operational Record - MIS User Activities Record")
//public class OperationalRecordController {
//    
//    @Resource
//    private OperationalRecordRemoteService operationalRecordRemoteService;
//
//    @ApiOperation(value = "Read all the activities records")
//    @PostMapping("/operational/list")
//    @RequiresPermissions("in:operationalRecord:*")
//    public Message<Page<Object>> getOperationalRecordLIst(@RequestBody OperationalRecordReqVo operationalRecordReqVo) {
//
//        RpcMessage<BigDecimal> totalMoneyRpcMessage = pivotFeeDetailRemoteService.getTotalMoneyByFeeType(Integer.valueOf(feeType));
//        if (totalMoneyRpcMessage.isSuccess()) {
//            return Message.success(totalMoneyRpcMessage.getContent().setScale(2, BigDecimal.ROUND_HALF_UP));
//        } else {
//            return Message.success();
//        }
//    }
//
//}
