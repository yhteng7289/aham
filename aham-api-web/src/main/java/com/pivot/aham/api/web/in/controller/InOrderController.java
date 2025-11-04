package com.pivot.aham.api.web.in.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pivot.aham.api.server.dto.req.OrderDetailReq;
import com.pivot.aham.api.server.dto.req.OrderDetailReq.OrderDetailR;
import com.pivot.aham.api.server.dto.resp.OrderDetailRes;
import com.pivot.aham.api.server.remoteservice.OrderServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.TradeExecuteRemoteService;
import com.pivot.aham.api.web.in.vo.AccountInfoReqVo;
import com.pivot.aham.api.web.in.vo.AhamReconReqVo;
import com.pivot.aham.api.web.web.controller.WebRegisterController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.dto.res.DropDownResDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Calendar;
import java.util.GregorianCalendar;

@RestController
@RequestMapping("/api/v1/in")
@Api(value = "首页接口", description = "首页接口")
public class InOrderController {
	
	private static final Logger log = LoggerFactory.getLogger(InOrderController.class);
	
	@Resource
	private TradeExecuteRemoteService tradeExecuteRemoteService;
	
	@Resource
	private OrderServiceRemoteService orderServiceRemoteService;

	@ApiOperation(value = "findOrderNumber")
    @PostMapping("/order/findOrderNumber")
    @RequiresPermissions("in:order:read")
    public List<DropDownResDTO> findOrderNumber() {
		List<DropDownResDTO> dropDownList = new ArrayList<DropDownResDTO>();
		RpcMessage<List<String>> rpcMessage = tradeExecuteRemoteService.findOrderNumber();
		if(rpcMessage.isSuccess()) {
    		List<String> orderNumberList = rpcMessage.getContent();
    		for(String orderNumber : orderNumberList) {
    			DropDownResDTO dropDownResDTO = new DropDownResDTO();
                dropDownResDTO.setLabel(orderNumber);
                dropDownResDTO.setValue(orderNumber);
                dropDownList.add(dropDownResDTO);
    		}
    	}
		return dropDownList;
	}
	
	@ApiOperation(value = "findOrderDetail")
    @PostMapping("/order/findOrderDetail")
    @RequiresPermissions("in:order:read")
    public List<OrderDetailRes> findOrderDetail(@RequestParam("orderNumber") String orderNumber) {
		List<OrderDetailRes> result = new ArrayList<OrderDetailRes>();
		RpcMessage<List<OrderDetailRes>> rpcMessage = tradeExecuteRemoteService.findOrderDetail(orderNumber);
		if(rpcMessage.isSuccess()) {
			result = rpcMessage.getContent();
		}
		return rpcMessage.getContent();
	}
	
    @ApiOperation(value = "submitOrderDetail")
    @PostMapping("/order/submitOrderDetail")
    public Message<String> submitOrderDetail(@RequestBody OrderDetailReq orderDetailReq) {
        log.info("=====orderDetailReq:{}", JSON.toJSON(orderDetailReq));
        log.info("=====getOrderDetailReqList:{}", JSON.toJSON(orderDetailReq.getOrderDetailReqList()));
        Calendar C = new GregorianCalendar();
        int hour = C.get( Calendar.HOUR_OF_DAY );
        int minute = C.get( Calendar.MINUTE );
        String result = "";
        boolean isError = false;
        if(hour < 18){
            JSONArray objectList = new JSONArray();
            for(OrderDetailR order : orderDetailReq.getOrderDetailReqList()) {
                HashMap<String, String> object = new HashMap<String, String>();
                object.put("id", String.valueOf(order.getId()));
                object.put("orderTypeAhamDesc", order.getOrderTypeAhamDesc());
                object.put("confirmShare", order.getConfirmShare()+"");
                object.put("confirmAmount", order.getConfirmAmount()+"");
                objectList.add(object);
            }
            log.info("=====objectList:{}", JSON.toJSON(objectList));
            RpcMessage<String> rpcMessage = tradeExecuteRemoteService.submitOrderDetail(objectList);
            if (rpcMessage.isSuccess() && rpcMessage.getResultCode() == 200) {
                    result = "Records update successfully.";
            } else {
                    result = "Records update failures. Please contact administrator.";
                    isError = true;
            }
        }else{
            result = "Please submit before 6pm";
            isError = true;
        }
        if(isError){
            return Message.error(result);
        }else{
            return Message.success(result);
        }
    }
	
	@ApiOperation(value = "Find Redeem Apply Page")
    @PostMapping("/order/findRedeemApplyPage")
    public Message<JSONArray> findRedeemApplyPage(@RequestBody AhamReconReqVo ahamReconReq) {
    	
    	Date startCreateTime = null;
    	Date endCreateTime = null;
    	
    	if(ahamReconReq.getStartCreateTime() != null) startCreateTime = ahamReconReq.getStartCreateTime();
    	if(ahamReconReq.getEndCreateTime() != null) endCreateTime = ahamReconReq.getEndCreateTime();
    	
    	JSONArray result = new JSONArray();
    	RpcMessage<JSONArray> rpcMessage = orderServiceRemoteService.findRedeemApplyPage(startCreateTime, endCreateTime);
    	if(rpcMessage.isSuccess()) {
    		result = rpcMessage.getContent();
    	}
        return Message.success(result);
    }
}
