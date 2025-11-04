package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.AccountInfoReqDTO;
import com.pivot.aham.api.server.dto.res.AccountInfoResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

public interface AccountInfoRemoteService extends BaseRemoteService {
    RpcMessage<Page<AccountInfoResDTO>> getAccountInfoPage(AccountInfoReqDTO accountInfoReqDTO);
    AccountInfoResDTO createNewAccIfNotExist(AccountInfoResDTO accountInfoResDTO);

}
