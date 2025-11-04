package com.pivot.aham.api.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferItem;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrder;
import com.pivot.aham.common.core.base.BaseService;
import com.pivot.aham.common.enums.analysis.VAOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;

import java.util.ArrayList;
import java.util.List;


public interface BankVirtualAccountOrderService extends BaseService<BankVirtualAccountOrder> {

    /**
     * 保存订单并且更行账户信息
     *
     * @param bankVirtualAccountOrders
     */
    void saveOrdersAndUpdateAccount(List<BankVirtualAccountOrder> bankVirtualAccountOrders, String clientId);

    List<BankVirtualAccountOrder> listBankVAOrders(BankVirtualAccountOrder bankVirtualAccountOrder);

    BankVirtualAccountOrder queryVAOrder(BankVirtualAccountOrder bankVirtualAccountOrder);

    /**
     * 用户订单查询
     *
     * @param virtualAccountNos
     * @param vaOrderTradeTypeEna
     * @return
     */
    List<BankVirtualAccountOrder> listUserOrders(List<String> virtualAccountNos, ArrayList<VAOrderTradeTypeEnum> vaOrderTradeTypeEna);

    void update(BankVirtualAccountOrder bankVirtualAccountOrder);

    BankVirtualAccountOrder queryVAOrderById(Long id);

    List<BankVirtualAccountOrder> listBankVirtualAccountOrders(BankVirtualAccountOrder order);
    Page<BankVirtualAccountOrder> listBankVirtualAccountOrderPage(BankVirtualAccountOrder order, Page<BankVirtualAccountOrder> rowBounds);
    List<BankVirtualAccountOrder> getListByTradeTime(BankVirtualAccountOrder params);

    BankVirtualAccountOrder getBVAOrder(ReceivedTransferItem receivedTransfer,
                                        VAOrderTradeStatusEnum orderTradeStatus,
                                        VAOrderTradeTypeEnum orderTradeType,
                                        VAOrderActionTypeEnum orderActionType,
                                        String referenceCode);

    void saveVAOrders(List<BankVirtualAccountOrder> vAOrderList);

    void haldelUobExchangeCallback(BankVirtualAccountOrder virtualAccountOrder,
                                   List<BankVirtualAccountOrder> vAOrderList,
                                   String clientId);

    BankVirtualAccountOrder queryFirstBVAOrder(String virtualAccountNo);
    
    BankVirtualAccountOrder queryLastBVAOrder(BankVirtualAccountOrder order); //Added by WooiTatt 

}
