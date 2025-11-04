package com.pivot.aham.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.req.ReceivedTransferReq;
import com.pivot.aham.api.server.dto.req.UobBalanceReq;
import com.pivot.aham.api.server.dto.req.UobExchangeReq;
import com.pivot.aham.api.server.dto.req.UobNotificationReq;
import com.pivot.aham.api.server.dto.req.UobTransferReq;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferItem;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferResult;
import com.pivot.aham.api.server.dto.resp.UobExchangeResult;
import com.pivot.aham.api.server.dto.resp.UobTransferResult;
import com.pivot.aham.api.service.UobRemoteSupportService;
import com.pivot.aham.api.service.mapper.UobBalanceMapper;
import com.pivot.aham.api.service.mapper.UobExchangeOrderMapper;
import com.pivot.aham.api.service.mapper.UobNotificationMapper;
import com.pivot.aham.api.service.mapper.UobRechargeLogMapper;
import com.pivot.aham.api.service.mapper.UobTransferOrderMapper;
import com.pivot.aham.api.service.mapper.model.UobBalancePO;
import com.pivot.aham.api.service.mapper.model.UobExchangeOrderPO;
import com.pivot.aham.api.service.mapper.model.UobNotificationPO;
import com.pivot.aham.api.service.mapper.model.UobRechargeLogPO;
import com.pivot.aham.api.service.mapper.model.UobTransferOrderPO;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.ExchangeOrderTypeEnum;
import com.pivot.aham.common.enums.UobOrderStatusEnum;
import com.pivot.aham.common.enums.UobTransferOrderTypeEnum;
import com.pivot.aham.common.enums.recharge.UobRechargeStatusEnum;

import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("uobRemoteSupportService")
@Slf4j
public class UobRemoteSupportServiceImpl implements UobRemoteSupportService {

    @Autowired
    private UobTransferOrderMapper uobTransferOrderMapper;

    @Autowired
    private UobExchangeOrderMapper uobExchangeOrderMapper;

    @Autowired
    private UobRechargeLogMapper uobRechargeLogMapper;

    @Autowired
    private UobBalanceMapper uobBalanceMapper;

    @Autowired
    private UobNotificationMapper uobNotificationMapper;

    @Override
    public RpcMessage<ReceivedTransferResult> queryReceivedTransferLog(ReceivedTransferReq receivedTransferReq) {
        List<ReceivedTransferItem> itemList = Lists.newArrayList();
        UobRechargeLogPO uobRechargeLogParam = new UobRechargeLogPO();
        uobRechargeLogParam.setRechargeStatus(receivedTransferReq.getRechargeStatus());
        List<UobRechargeLogPO> rechargeLogList = uobRechargeLogMapper.queryList(uobRechargeLogParam);
        for (UobRechargeLogPO rechargeLog : rechargeLogList) {
            ReceivedTransferItem transferLog = new ReceivedTransferItem();
            transferLog.setClientName(rechargeLog.getClientName());
            transferLog.setVirtualAccountNo(rechargeLog.getVirtualAccountNo());
            transferLog.setCurrency(rechargeLog.getCurrency());
            transferLog.setReferenceCode(rechargeLog.getReferenceCode());
            transferLog.setCashAmount(rechargeLog.getCashAmount());
            transferLog.setBankOrderNo(rechargeLog.getBankOrderNo());
            transferLog.setTradeTime(rechargeLog.getTradeTime());
            itemList.add(transferLog);
        }

        ReceivedTransferResult result = new ReceivedTransferResult();
        result.setItemList(itemList);
        return RpcMessage.success(result);
    }

    @Override
    public RpcMessage<ReceivedTransferItem> queryByBankOrderNo(ReceivedTransferReq receivedTransferReq) {
        UobRechargeLogPO rechargeLog = uobRechargeLogMapper.queryByBankOrderNo(receivedTransferReq.getBankOrderNo());
        ReceivedTransferItem transferLog = new ReceivedTransferItem();
        transferLog.setClientName(rechargeLog.getClientName());
        transferLog.setVirtualAccountNo(rechargeLog.getVirtualAccountNo());
        transferLog.setCurrency(rechargeLog.getCurrency());
        transferLog.setReferenceCode(rechargeLog.getReferenceCode());
        transferLog.setCashAmount(rechargeLog.getCashAmount());
        transferLog.setBankOrderNo(rechargeLog.getBankOrderNo());
        transferLog.setTradeTime(rechargeLog.getTradeTime());
        return RpcMessage.success(transferLog);
    }

    @Override
    public RpcMessage<UobTransferResult> transferToSaxo(UobTransferReq uobTransferReq) {
        UobTransferOrderPO order = this.saveUobTransferOrder(uobTransferReq, UobTransferOrderTypeEnum.TRANSFER_TO_SAXO);
        UobTransferResult result = new UobTransferResult();
        result.setOrderId(order.getId());
        return RpcMessage.success(result);
    }

    public RpcMessage<UobExchangeResult> exchangeForSaxo(UobExchangeReq uobExchangeReq) {
        return this.exchange(uobExchangeReq, ExchangeOrderTypeEnum.RECHARGE);
    }

    public RpcMessage<UobExchangeResult> exchangeForClient(UobExchangeReq uobExchangeReq) {
        return this.exchange(uobExchangeReq, ExchangeOrderTypeEnum.WITHDRAW);
    }

    private RpcMessage<UobExchangeResult> exchange(UobExchangeReq uobExchangeReq, ExchangeOrderTypeEnum orderType) {
        UobExchangeOrderPO uobExchangeOrderPO = new UobExchangeOrderPO();
        uobExchangeOrderPO.setId(Sequence.next());
        uobExchangeOrderPO.setOutBusinessId(uobExchangeReq.getOutBusinessId());
        uobExchangeOrderPO.setOrderType(orderType);
        uobExchangeOrderPO.setExchangeType(uobExchangeReq.getExchangeType());
        uobExchangeOrderPO.setOrderStatus(UobOrderStatusEnum.WAIT_EXECUTE);
        uobExchangeOrderPO.setApplyAmount(uobExchangeReq.getExchangeAmount());
        uobExchangeOrderPO.setApplyTime(DateUtils.now());
        uobExchangeOrderMapper.save(uobExchangeOrderPO);

        UobExchangeResult result = new UobExchangeResult();
        result.setOrderId(uobExchangeOrderPO.getId());
        result.setConfirmAmount(uobExchangeReq.getExchangeAmount());
        return RpcMessage.success(result);
    }

    @Override
    public RpcMessage<UobTransferResult> withdrawToBankCard(UobTransferReq uobTransferReq) {
        UobTransferOrderPO order = this.saveUobTransferOrder(uobTransferReq, UobTransferOrderTypeEnum.TRANSFER_TO_BANK);
        UobTransferResult result = new UobTransferResult();
        result.setOrderId(order.getId());
        return RpcMessage.success(result);
    }

    @Override
    public void callBackUobRechargeSuccess(List<ReceivedTransferItem> transferSuccessItems) {
        log.info("处理成功的UOB充值单回调处理,transferSuccessItems:{}", JSON.toJSONString(transferSuccessItems));
        for (ReceivedTransferItem receivedTransferItem : transferSuccessItems) {
            try {
                UobRechargeLogPO uobRechargeLogPO = new UobRechargeLogPO();
                uobRechargeLogPO.setRechargeStatus(UobRechargeStatusEnum.SUCCESS);
                uobRechargeLogPO.setBankOrderNo(receivedTransferItem.getBankOrderNo());
                uobRechargeLogMapper.updateByBankOrderNo(uobRechargeLogPO);
            } catch (Exception ex) {
                log.error("处理成功的充值单回调,receivedTransferItem:{},异常:", JSON.toJSONString(receivedTransferItem), ex);
            }
        }
    }

    private UobTransferOrderPO saveUobTransferOrder(UobTransferReq uobTransferReq, UobTransferOrderTypeEnum orderType) {
        UobTransferOrderPO order = new UobTransferOrderPO();
        order.setId(Sequence.next());
        order.setOutBusinessId(uobTransferReq.getOutBusinessId());
        order.setOrderType(orderType);
        order.setOrderStatus(UobOrderStatusEnum.WAIT_CREATE_ORDER);
        order.setAmount(uobTransferReq.getAmount());
        order.setApplyTime(DateUtils.now());
        order.setCurrency(uobTransferReq.getCurrency());
        order.setRemark(uobTransferReq.getRemark());

        if (orderType == UobTransferOrderTypeEnum.TRANSFER_TO_SAXO) {
            order.setBankName("HSBC(Corporate)");
            order.setBankAccountNumber("147-125793-003");
            order.setBankUserName("Saxo Capital Markets Pte.Ltd.");
            order.setBranchCode("");
            order.setSwiftCode("HSBCSGSG");
        }

        if (orderType == UobTransferOrderTypeEnum.TRANSFER_TO_BANK) {
            order.setBankName(uobTransferReq.getBankName());
            order.setBankAccountNumber(uobTransferReq.getBankAccountNumber());
            order.setBankUserName(uobTransferReq.getBankUserName());
            order.setBranchCode(uobTransferReq.getBranchCode());
            order.setSwiftCode(uobTransferReq.getSwiftCode());
        }

        uobTransferOrderMapper.save(order);
        return order;
    }

    @Override
    public RpcMessage<List<ReceivedTransferItem>> queryProcessingByVirtualAccountNo(String virtualAccountNo, String referenceCode) {
        List<UobRechargeLogPO> uobRechargeLogPOList = uobRechargeLogMapper.queryProcessingByVirtualAccountNo(virtualAccountNo, referenceCode);
        List<ReceivedTransferItem> receivedTransferItem = new ArrayList();
        uobRechargeLogPOList.forEach((uobRechargeLogPO) -> {
            ReceivedTransferItem transferLog = new ReceivedTransferItem();
            transferLog.setClientName(uobRechargeLogPO.getClientName());
            transferLog.setVirtualAccountNo(uobRechargeLogPO.getVirtualAccountNo());
            transferLog.setCurrency(uobRechargeLogPO.getCurrency());
            transferLog.setReferenceCode(uobRechargeLogPO.getReferenceCode());
            transferLog.setCashAmount(uobRechargeLogPO.getCashAmount());
            transferLog.setBankOrderNo(uobRechargeLogPO.getBankOrderNo());
            transferLog.setTradeTime(uobRechargeLogPO.getTradeTime());
            receivedTransferItem.add(transferLog);
        });
        return RpcMessage.success(receivedTransferItem);
    }

    @Override
    public void insertUobBalanceRecords(UobBalanceReq uobBalanceReq) {

        UobBalancePO uobBalancePO = new UobBalancePO();
        uobBalancePO.setAccountBalanceAmount(uobBalanceReq.getAccountBalanceAmount());
        uobBalancePO.setAccountBalanceCurrency(uobBalanceReq.getAccountBalanceCurrency());
        uobBalancePO.setAccountCurrency(uobBalanceReq.getAccountCurrency());
        uobBalancePO.setAccountName(uobBalanceReq.getAccountName());
        uobBalancePO.setAccountNumber(uobBalanceReq.getAccountNumber());
        uobBalancePO.setAccountType(uobBalanceReq.getAccountType());
        uobBalancePO.setAvailableBalanceAmount(uobBalanceReq.getAvailableBalanceAmount());
        uobBalancePO.setAvailableBalanceCurrency(uobBalanceReq.getAvailableBalanceCurrency());
        uobBalancePO.setBranch(uobBalanceReq.getBranch());
        uobBalancePO.setCreateTime(DateUtils.now());
        uobBalancePO.setLedgerBalanceAmount(uobBalanceReq.getLedgerBalanceAmount());
        uobBalancePO.setLedgerBalanceCurrency(uobBalanceReq.getLedgerBalanceCurrency());
        uobBalancePO.setMasterAccountNumberForSubAccount(uobBalanceReq.getMasterAccountNumberForSubAccount());
        uobBalancePO.setOdDrawingLimit(uobBalanceReq.getOdDrawingLimit());
        uobBalancePO.setSamcPrimaryAccountIndicator(uobBalanceReq.getSamcPrimaryAccountIndicator());
        uobBalancePO.setSubAccountAllocatedBalance(uobBalanceReq.getSubAccountAllocatedBalance());
        uobBalancePO.setTodayCredit(uobBalanceReq.getTodayCredit());
        uobBalancePO.setTodayDebit(uobBalanceReq.getTodayDebit());
        uobBalancePO.setTotalAvailabilityFloat(uobBalanceReq.getTotalAvailabilityFloat());
        uobBalancePO.setUpdateTime(DateUtils.now());

        uobBalanceMapper.saveUobBalanace(uobBalancePO);

    }

    @Override
    public void uobNotificationCallback(UobNotificationReq uobNotificationReq) {
        UobNotificationPO uobNotificationPO = new UobNotificationPO();

        uobNotificationPO.setAccountCurrency(uobNotificationReq.getAccountCurrency());
        uobNotificationPO.setAccountName(uobNotificationReq.getAccountName());
        uobNotificationPO.setAccountNumber(uobNotificationReq.getAccountNumber());
        uobNotificationPO.setAccountType(uobNotificationReq.getAccountType());
        uobNotificationPO.setAmount(uobNotificationReq.getAmount());
        uobNotificationPO.setBusinessDate(uobNotificationReq.getBusinessDate());
        uobNotificationPO.setCreateTime(DateUtils.now());
        uobNotificationPO.setEffectiveDate(uobNotificationReq.getEffectiveDate());
        uobNotificationPO.setEvent(uobNotificationReq.getEvent());
        uobNotificationPO.setInstructionId(uobNotificationReq.getInstructionId());
        uobNotificationPO.setNotificationId(uobNotificationReq.getNotificationId());
        uobNotificationPO.setOriginatorAccountName(uobNotificationReq.getOriginatorAccountName());
        uobNotificationPO.setOurReference(uobNotificationReq.getOurReference());
        uobNotificationPO.setPayNowIndicator(uobNotificationReq.getPayNowIndicator());
        uobNotificationPO.setRemittanceInformation(uobNotificationReq.getRemittanceInformation());

        uobNotificationPO.setSubAccountIndicator(uobNotificationReq.getSubAccountIndicator());
        uobNotificationPO.setTransactionDateTime(uobNotificationReq.getTransactionDateTime());
        uobNotificationPO.setTransactionText(uobNotificationReq.getTransactionText());
        uobNotificationPO.setTransactionType(uobNotificationReq.getTransactionType());
        uobNotificationPO.setUpdateTime(DateUtils.now());
        uobNotificationPO.setYourReference(uobNotificationReq.getYourReference());

        uobNotificationMapper.saveUobNotification(uobNotificationPO);
    }

}
