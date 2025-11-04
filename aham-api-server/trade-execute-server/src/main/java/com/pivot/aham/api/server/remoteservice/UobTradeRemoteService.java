package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.resp.ReceivedTransferItem;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferResult;
import com.pivot.aham.api.server.dto.resp.UobExchangeResult;
import com.pivot.aham.api.server.dto.resp.UobTransferResult;
import com.pivot.aham.api.server.dto.req.ReceivedTransferReq;
import com.pivot.aham.api.server.dto.req.UobBalanceReq;
import com.pivot.aham.api.server.dto.req.UobExchangeReq;
import com.pivot.aham.api.server.dto.req.UobNotificationReq;
import com.pivot.aham.api.server.dto.req.UobTransferReq;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

/**
 * Created by hao.tong on 2018/12/6.
 */
public interface UobTradeRemoteService extends BaseRemoteService {

    /**
     * 获取所有收款记录
     */
    RpcMessage<ReceivedTransferResult> queryReceivedTransferLog(ReceivedTransferReq receivedTransferReq);

    /**
     * 转账到SAXO
     */
    RpcMessage<UobTransferResult> transferToSaxo(UobTransferReq uobTransferReq);

    /**
     * 换汇
     */
    RpcMessage<UobExchangeResult> exchangeForRecharge(UobExchangeReq uobExchangeReq);

    RpcMessage<UobExchangeResult> exchangeForWithdraw(UobExchangeReq uobExchangeReq);

    /**
     * 提现到银行卡
     */
    RpcMessage<UobTransferResult> withdrawToBankCard(UobTransferReq uobTransferReq);

    /**
     * UOB充值回调修改处理状态
     *
     * @param transferSuccessItems
     */
    void callBackUobRechargeSuccess(List<ReceivedTransferItem> transferSuccessItems);

    RpcMessage<ReceivedTransferItem> queryByBankOrderNo(ReceivedTransferReq receivedTransferReq);

    RpcMessage<List<ReceivedTransferItem>> queryProcessingByVirtualAccountNo(String virtualAccountNo, String referenceCode);

    void insertUobBalanceRecords(UobBalanceReq uobBalanceReqDTO);

    void uobNotificationCallback(UobNotificationReq uobBalanceReqDTO);

}
