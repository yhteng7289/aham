package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.req.AccountStaticsReqDTO;
import com.pivot.aham.api.server.dto.res.AccountStaticsResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

public interface AccountStaticsRemoteService extends BaseRemoteService {

    RpcMessage<List<AccountStaticsResDTO>> getUserStatics(AccountStaticsReqDTO accountStaticsReqDTO);

    RpcMessage<AccountStaticsResDTO> getLastUserStatics(AccountStaticsReqDTO accountStaticsReqDTO);

    RpcMessage<AccountStaticsResDTO> selectByStaticDate(AccountStaticsReqDTO accountStaticsReqDTO);

}
