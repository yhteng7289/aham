package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.SaxoAccountOrderReqDTO;
import com.pivot.aham.api.server.dto.SaxoAccountOrderResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

public interface SaxoAccountOrderRemoteService extends BaseRemoteService {
    RpcMessage<List<SaxoAccountOrderResDTO>> getSaxoAccountOrders(SaxoAccountOrderReqDTO saxoAccountOrderReqDTO);
    RpcMessage<SaxoAccountOrderResDTO> getSaxoAccountOrder(SaxoAccountOrderReqDTO saxoAccountOrderReqDTO);
}
