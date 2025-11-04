package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.UobTransferToSaxoCallbackDTO;
import com.pivot.aham.api.server.dto.res.AccountRechargeResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

/**
 * 只用作测试
 *
 * @author addison
 * @since 2018年12月10日
 */
public interface RechargeServiceRemoteService extends BaseRemoteService {

    /**
     * 合并转账成功之后的回调(UOB --> SAXO)
     */
    RpcMessage rechargeUobTransferToSaxoCallback(List<UobTransferToSaxoCallbackDTO> params);
    
    /**
     * 合并转账成功之后的回调(UOB --> SAXO)
     */
    RpcMessage rechargeUobTransferToSaxoCallback(String orderId, List<UobTransferToSaxoCallbackDTO> params);

    /**
     * 资产从UOB转账进入SAXO回调
     */
    void uboTransferSaxoCallback();

    /**
     * 申购交易分析下etf单
     */
    void tradeAnalysisJob(String accountId);


    void rechargeAhamTransferList(List<AccountRechargeResDTO> accountRechargeResDTOList);

    void rechargeAhamTransfer(AccountRechargeResDTO accountRechargeResDTO);


}
