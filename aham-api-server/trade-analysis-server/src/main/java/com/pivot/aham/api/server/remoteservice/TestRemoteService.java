package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月10日
 */
public interface TestRemoteService extends BaseRemoteService{
    RpcMessage withdrawalSaxoToUob();
    RpcMessage withdrawalUobToBankTransfer();

    void uboExchangeCallback(Long vaOrderId);

    void staticAccountEtfJob(Date date);
    void staticUserEtfJob(Date date);

    /**
     * 清除register用户信息 user_info & bank_virtual_account
     *
     * @param clientId
     */
    void cleanRegister(Integer clientId);

    void deleteFromTable(String tableName, Long id);

    void updateClientName(String clientName, Long id);

    void updateVACash(BigDecimal cashAmount, BigDecimal freezeAmount, BigDecimal usedAmount, Long id);

    void updateSaxoStatu(Integer status, Long id);

    void updateBankVAStatu(Integer status, Long id);

    void updateAccountStatics(Long accountId,Date date);

    void uobTransferToSaxoJob();

    void userProfit(Date date);

    void calCustStatement(String clientId,Integer monthOffset);

    void fixCustStatement();

}
