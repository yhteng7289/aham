package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.req.AccountetfSharesReqDTO;
import com.pivot.aham.api.server.dto.res.AccountetfSharesResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

public interface AccountEtfSharesRemoteService extends BaseRemoteService {

    RpcMessage<List<AccountetfSharesResDTO>> getAccountEtfShares(AccountetfSharesReqDTO accountetfSharesReqDTO);

    RpcMessage<List<AccountetfSharesResDTO>> selectByStaticDate(AccountetfSharesReqDTO accountEtfSharesPO);

    RpcMessage<AccountetfSharesResDTO> selectByStaticDateByAccountId(AccountetfSharesReqDTO accountEtfSharesPO);

}
