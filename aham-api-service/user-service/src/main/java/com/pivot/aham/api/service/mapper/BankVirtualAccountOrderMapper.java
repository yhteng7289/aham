package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrder;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;


/**
 * @author addison
 */
public interface BankVirtualAccountOrderMapper extends BaseMapper<BankVirtualAccountOrder> {

    void insertBatch(List<BankVirtualAccountOrder> bankVirtualAccountOrders);

    List<BankVirtualAccountOrder> listBankVAOrders(BankVirtualAccountOrder bankVirtualAccountOrder);

    BankVirtualAccountOrder queryVAOrder(BankVirtualAccountOrder bankVirtualAccountOrder);

    List<BankVirtualAccountOrder> listUserOrders(@Param("virtualAccountNos") List<String> virtualAccountNos,
                                                 @Param("vaOrderTradeTypeEna") ArrayList<VAOrderTradeTypeEnum> vaOrderTradeTypeEna);

    void update(BankVirtualAccountOrder bankVirtualAccountOrder);

    BankVirtualAccountOrder queryVAOrderById(@Param("id") Long id);

    List<BankVirtualAccountOrder> listBankVirtualAccountOrders(BankVirtualAccountOrder order);

    List<BankVirtualAccountOrder> listBankVirtualAccountOrderPage(Page<BankVirtualAccountOrder> rowBounds,BankVirtualAccountOrder order);

    BankVirtualAccountOrder queryFirstBVAOrder(@Param("virtualAccountNo") String virtualAccountNo);

    List<BankVirtualAccountOrder> getListByTradeTime(BankVirtualAccountOrder params);
    
    BankVirtualAccountOrder queryLastBVAOrder(BankVirtualAccountOrder order); //Added by WooiTatt 

}