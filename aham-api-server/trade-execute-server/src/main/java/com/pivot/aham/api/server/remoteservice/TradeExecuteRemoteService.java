package com.pivot.aham.api.server.remoteservice;

import java.util.List;

import com.pivot.aham.api.server.dto.req.OrderDetailReq;
import com.pivot.aham.api.server.dto.req.OrderDetailReq.OrderDetailR;
import com.pivot.aham.api.server.dto.resp.OrderDetailRes;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.alibaba.dubbo.common.json.JSONObject;
import com.alibaba.fastjson.JSONArray;

public interface TradeExecuteRemoteService {

	RpcMessage<List<String>> findOrderNumber();

	RpcMessage<List<OrderDetailRes>> findOrderDetail(String orderNumber);
	
	RpcMessage<String> submitOrderDetail(JSONArray orderDetailList);
}
