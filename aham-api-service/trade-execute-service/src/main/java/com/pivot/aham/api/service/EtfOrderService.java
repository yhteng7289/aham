package com.pivot.aham.api.service;

import com.pivot.aham.api.server.dto.resp.SaxoTradeResult;
import com.pivot.aham.api.server.dto.req.SaxoTradeReq;
import com.pivot.aham.common.core.base.RpcMessage;

public interface EtfOrderService {
    RpcMessage<SaxoTradeResult> createBuyOrder(SaxoTradeReq saxoTradeReq);
    RpcMessage<SaxoTradeResult> createSellOrder(SaxoTradeReq saxoTradeReq);
}
