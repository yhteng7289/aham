package com.pivot.aham.api.service.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferItem;
import com.pivot.aham.api.service.mapper.BankVirtualAccountOrderMapper;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccount;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrder;
import com.pivot.aham.api.service.service.BankVirtualAccountOrderService;
import com.pivot.aham.api.service.service.BankVirtualAccountService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.analysis.NeedRefundTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankVirtualAccountOrderServiceImpl extends BaseServiceImpl<BankVirtualAccountOrder, BankVirtualAccountOrderMapper> implements BankVirtualAccountOrderService {

    @Resource
    private BankVirtualAccountService bankVirtualAccountService;

    @Override
    @Transactional
    public void saveOrdersAndUpdateAccount(List<BankVirtualAccountOrder> bankVirtualAccountOrders, String clientId) {
        if (CollectionUtils.isEmpty(bankVirtualAccountOrders)) {
            return;
        }
        mapper.insertBatch(bankVirtualAccountOrders);
        //重新计算虚拟现金账户资产
        BankVirtualAccount queryParam = new BankVirtualAccount();
        queryParam.setClientId(clientId);
        List<BankVirtualAccount> bankVirtualAccounts = bankVirtualAccountService.queryListByClient(queryParam);
        for (BankVirtualAccount virtualAccount : bankVirtualAccounts) {
            BankVirtualAccount bankVirtualAccount = new BankVirtualAccount();
            bankVirtualAccount.setVirtualAccountNo(virtualAccount.getVirtualAccountNo());
            bankVirtualAccountService.statisticsAmount(bankVirtualAccount);
        }

    }

    @Override
    public List<BankVirtualAccountOrder> listBankVAOrders(BankVirtualAccountOrder bankVirtualAccountOrder) {
        return mapper.listBankVAOrders(bankVirtualAccountOrder);
    }

    @Override
    public BankVirtualAccountOrder queryVAOrder(BankVirtualAccountOrder bankVirtualAccountOrder) {
        return mapper.queryVAOrder(bankVirtualAccountOrder);
    }

    @Override
    public List<BankVirtualAccountOrder> listUserOrders(List<String> virtualAccountNos, ArrayList<VAOrderTradeTypeEnum> vaOrderTradeTypeEna) {
        return mapper.listUserOrders(virtualAccountNos, vaOrderTradeTypeEna);
    }

    @Override
    public void update(BankVirtualAccountOrder bankVirtualAccountOrder) {
        mapper.update(bankVirtualAccountOrder);
    }

    @Override
    public BankVirtualAccountOrder queryVAOrderById(Long id) {
        return mapper.queryVAOrderById(id);
    }

    @Override
    public List<BankVirtualAccountOrder> listBankVirtualAccountOrders(BankVirtualAccountOrder order) {
        return mapper.listBankVirtualAccountOrders(order);
    }

    @Override
    public Page<BankVirtualAccountOrder> listBankVirtualAccountOrderPage(BankVirtualAccountOrder order, Page<BankVirtualAccountOrder> rowBounds) {
        List<BankVirtualAccountOrder> ts =  mapper.listBankVirtualAccountOrderPage(rowBounds,order);
        rowBounds.setRecords(ts);
        return rowBounds;
    }


    @Override
    public BankVirtualAccountOrder getBVAOrder(ReceivedTransferItem item,
                                               VAOrderTradeStatusEnum orderTradeStatus,
                                               VAOrderTradeTypeEnum orderTradeType,
                                               VAOrderActionTypeEnum orderActionType,
                                               String referenceCode) {
        BankVirtualAccountOrder order = new BankVirtualAccountOrder();
        order
                .setRedeemApplyId(0L)
                .setActionType(orderActionType)
                .setReferenceCode(referenceCode)
                .setVirtualAccountNo(item.getVirtualAccountNo())
                .setCashAmount(item.getCashAmount())
                .setCurrency(item.getCurrency())
                .setOperatorType(orderTradeType)
                .setBankOrderNo(item.getBankOrderNo())
                .setOrderStatus(orderTradeStatus)
                .setNeedRefundType(NeedRefundTypeEnum.UN_REFUND)
//                .setMatchType(MatchTypeEnum.MATCH)
                .setTradeTime(item.getTradeTime())
                .setCreateTime(DateUtils.now())
                .setUpdateTime(DateUtils.now())
                .setId(Sequence.next());
        return order;
    }

    @Override
    public void saveVAOrders(List<BankVirtualAccountOrder> vAOrderList) {
        mapper.insertBatch(vAOrderList);
    }

    @Override
    @Transactional
    public void haldelUobExchangeCallback(BankVirtualAccountOrder virtualAccountOrder,
                                          List<BankVirtualAccountOrder> bankVirtualAccountOrders,
                                          String clientId) {
        update(virtualAccountOrder);
        if (CollectionUtils.isEmpty(bankVirtualAccountOrders)) {
            return;
        }
        mapper.insertBatch(bankVirtualAccountOrders);
        //重新计算虚拟现金账户资产
        BankVirtualAccount queryParam = new BankVirtualAccount();
        queryParam.setClientId(clientId);
        List<BankVirtualAccount> bankVirtualAccounts = bankVirtualAccountService.queryListByClient(queryParam);
        for (BankVirtualAccount virtualAccount : bankVirtualAccounts) {
            BankVirtualAccount bankVirtualAccount = new BankVirtualAccount();
            bankVirtualAccount.setVirtualAccountNo(virtualAccount.getVirtualAccountNo());
            bankVirtualAccountService.statisticsAmount(bankVirtualAccount);
        }
    }

    @Override
    public BankVirtualAccountOrder queryFirstBVAOrder(String virtualAccountNo) {
        return mapper.queryFirstBVAOrder(virtualAccountNo);
    }
    @Override
    public List<BankVirtualAccountOrder> getListByTradeTime(BankVirtualAccountOrder params) {
        return mapper.getListByTradeTime(params);
    }
    
    //Added By WooiTatt 
    @Override
    public BankVirtualAccountOrder queryLastBVAOrder(BankVirtualAccountOrder order) {
        return mapper.queryLastBVAOrder(order);
    }
}
