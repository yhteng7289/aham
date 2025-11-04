package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.req.ReceivedTransferReq;
import com.pivot.aham.api.server.dto.req.UobBalanceReq;
import com.pivot.aham.api.server.dto.req.UobExchangeReq;
import com.pivot.aham.api.server.dto.req.UobNotificationReq;
import com.pivot.aham.api.server.dto.req.UobTransferReq;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferItem;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferResult;
import com.pivot.aham.api.server.dto.resp.UobExchangeResult;
import com.pivot.aham.api.server.dto.resp.UobTransferResult;
import com.pivot.aham.api.server.remoteservice.UobTradeRemoteService;
import com.pivot.aham.api.service.UobRemoteSupportService;
import com.pivot.aham.common.core.base.RpcMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hao.tong on 2018/12/11.
 */
@Service(interfaceClass = UobTradeRemoteService.class)
@Slf4j
public class UobTradeRemoteServiceImpl implements UobTradeRemoteService {

    @Autowired
    private UobRemoteSupportService uobRemoteSupportService;

    @Override
    public RpcMessage<ReceivedTransferResult> queryReceivedTransferLog(ReceivedTransferReq receivedTransferReq) {
        return uobRemoteSupportService.queryReceivedTransferLog(receivedTransferReq);
    }

    @Override
    public RpcMessage<UobTransferResult> transferToSaxo(UobTransferReq uobTransferReq) {
        return uobRemoteSupportService.transferToSaxo(uobTransferReq);
    }

    @Override
    public RpcMessage<UobExchangeResult> exchangeForRecharge(UobExchangeReq uobExchangeReq) {
        return uobRemoteSupportService.exchangeForSaxo(uobExchangeReq);
    }

    @Override
    public RpcMessage<UobExchangeResult> exchangeForWithdraw(UobExchangeReq uobExchangeReq) {
        return uobRemoteSupportService.exchangeForClient(uobExchangeReq);
    }

    @Override
    public RpcMessage<UobTransferResult> withdrawToBankCard(UobTransferReq uobTransferReq) {
        return uobRemoteSupportService.withdrawToBankCard(uobTransferReq);
    }

    @Override
    public void callBackUobRechargeSuccess(List<ReceivedTransferItem> transferSuccessItems) {
        uobRemoteSupportService.callBackUobRechargeSuccess(transferSuccessItems);
    }

    @Override
    public RpcMessage<ReceivedTransferItem> queryByBankOrderNo(ReceivedTransferReq receivedTransferReq) {
        return uobRemoteSupportService.queryByBankOrderNo(receivedTransferReq);
    }

    @Override
    public RpcMessage<List<ReceivedTransferItem>> queryProcessingByVirtualAccountNo(String virtualAccountNo, String referenceCode) {
        return uobRemoteSupportService.queryProcessingByVirtualAccountNo(virtualAccountNo, referenceCode);
    }

    @Override
    public void insertUobBalanceRecords(UobBalanceReq uobBalanceReqDTO) {
        uobRemoteSupportService.insertUobBalanceRecords(uobBalanceReqDTO);
    }

    @Override
    public void uobNotificationCallback(UobNotificationReq uobBalanceReqDTO) {
        uobRemoteSupportService.uobNotificationCallback(uobBalanceReqDTO);
    }
}
