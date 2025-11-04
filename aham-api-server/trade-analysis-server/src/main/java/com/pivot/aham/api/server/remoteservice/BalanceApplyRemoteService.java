package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.req.BalanceApplyReqDTO;
import com.pivot.aham.api.server.dto.res.BalanceApplyResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

public interface BalanceApplyRemoteService extends BaseRemoteService {
    RpcMessage<BalanceApplyResDTO> queryBalanceByApplyDate(BalanceApplyReqDTO balanceApplyReqDTO);
}
