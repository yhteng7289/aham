package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.UserStaticsReqDTO;
import com.pivot.aham.api.server.dto.UserStaticsResDTO;
import com.pivot.aham.api.server.dto.req.UserEtfSharesReqDTO;
import com.pivot.aham.api.server.dto.req.UserProfitInfoReqDTO;
import com.pivot.aham.api.server.dto.res.UserEtfSharesResDTO;
import com.pivot.aham.api.server.dto.res.UserProfitInfoResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

public interface UserStaticsRemoteService extends BaseRemoteService {
    RpcMessage<List<UserStaticsResDTO>> getUserStatics(UserStaticsReqDTO userStaticsReqDTO);

    RpcMessage<UserStaticsResDTO> getUserStatic(UserStaticsReqDTO userStaticsReqDTO);

    RpcMessage<List<UserProfitInfoResDTO>> getUserProfitInfos(UserProfitInfoReqDTO userProfitInfoReqDTO);

    RpcMessage<List<UserEtfSharesResDTO>> getUserEtfShares(UserEtfSharesReqDTO userEtfSharesReqDTO);
}
