package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.BankNameAliasReqDTO;
import com.pivot.aham.api.server.dto.BankNameAliasResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;


/**
 * Created by WooiTatt
 */
public interface NameAliasRemoteService extends BaseRemoteService {
    
    RpcMessage<Page<BankNameAliasResDTO>> getBankNameAliasPage(BankNameAliasReqDTO bankNameAliasReqDTO);
    
    RpcMessage<BankNameAliasResDTO> queryClientInfo(String rechargeId); 
    
    void approvedNameAlias(BankNameAliasReqDTO bankNameAliasReqDTO);
    
    void updateRejection(BankNameAliasReqDTO bankNameAliasReqDTO);

    
}
