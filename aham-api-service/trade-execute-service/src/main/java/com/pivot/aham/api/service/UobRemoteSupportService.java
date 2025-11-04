package com.pivot.aham.api.service;

import com.pivot.aham.api.server.dto.req.ReceivedTransferReq;
import com.pivot.aham.api.server.dto.req.UobBalanceReq;
import com.pivot.aham.api.server.dto.req.UobExchangeReq;
import com.pivot.aham.api.server.dto.req.UobNotificationReq;
import com.pivot.aham.api.server.dto.req.UobTransferReq;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferItem;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferResult;
import com.pivot.aham.api.server.dto.resp.UobExchangeResult;
import com.pivot.aham.api.server.dto.resp.UobTransferResult;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

public interface UobRemoteSupportService {

    RpcMessage<ReceivedTransferResult> queryReceivedTransferLog(ReceivedTransferReq receivedTransferReq);

    RpcMessage<UobTransferResult> transferToSaxo(UobTransferReq uobTransferReq);

    RpcMessage<UobExchangeResult> exchangeForSaxo(UobExchangeReq uobExchangeReq);

    RpcMessage<UobExchangeResult> exchangeForClient(UobExchangeReq uobExchangeReq);

    RpcMessage<UobTransferResult> withdrawToBankCard(UobTransferReq uobTransferReq);

    void callBackUobRechargeSuccess(List<ReceivedTransferItem> transferSuccessItems);

    RpcMessage<ReceivedTransferItem> queryByBankOrderNo(ReceivedTransferReq receivedTransferReq);

    RpcMessage<List<ReceivedTransferItem>> queryProcessingByVirtualAccountNo(String virtualAccountNo, String referenceCode);

    void insertUobBalanceRecords(UobBalanceReq uobBalanceReqDTO);

    void uobNotificationCallback(UobNotificationReq uobBalanceReqDTO);

}
