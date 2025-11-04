package com.pivot.aham.api.server.remoteservice;

import java.util.List;

import com.pivot.aham.api.server.dto.req.UobRechargeReq;
import com.pivot.aham.common.core.base.RpcMessage;

/**
 * Created by dexter on 17/4/2020
 */
public interface UobSaveRechargeLogRemoteService {

    public String runRechargeLog();
//    public void runRechargeLog();

    public RpcMessage<String> insertRechargeLog(List<UobRechargeReq> uobRechargeReq);

    public RpcMessage<String> insertOneRechargeLog(UobRechargeReq uobRechargeReq);

}
