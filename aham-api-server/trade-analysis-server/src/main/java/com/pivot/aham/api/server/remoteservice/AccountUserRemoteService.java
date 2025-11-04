package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.req.AccountUserReqDTO;
import com.pivot.aham.api.server.dto.res.AccountUserResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

public interface AccountUserRemoteService extends BaseRemoteService {
    RpcMessage<List<AccountUserResDTO>> getAccountUserList(AccountUserReqDTO accountUserReqDTO);

}
