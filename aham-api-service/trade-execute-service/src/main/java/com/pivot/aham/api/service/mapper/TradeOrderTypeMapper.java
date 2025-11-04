package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.SaxoOrderPO;
import com.pivot.aham.api.service.mapper.model.TradeOrderTypePO;
import com.pivot.aham.common.enums.SaxoOrderFeeStatusEnum;
import com.pivot.aham.common.enums.SaxoOrderStatusEnum;
import com.pivot.aham.common.enums.SaxoOrderTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface TradeOrderTypeMapper {
    void save(TradeOrderTypePO tradeOrderTypePO);
    
    TradeOrderTypePO getTradeOrderTypePOBySaxoCode(TradeOrderTypePO tradeOrderTypePO);

   /* SaxoOrderPO getById(@Param("orderId") Long orderId);

    SaxoOrderPO getByMergeOrderId(@Param("mergeOrderId") Long mergeOrderId);

    List<SaxoOrderPO> getOrderList(@Param("orderType") SaxoOrderTypeEnum orderType, @Param("orderStatus") SaxoOrderStatusEnum orderStatus);

    void confirmOrder(
            @Param("orderId") Long orderId,
            @Param("confirmShare") Integer confirmShare,
            @Param("confirmAmount") BigDecimal confirmAmount,
            @Param("confirmTime") Date confirmTime,
            @Param("commission") BigDecimal commission,
            @Param("exchangeFee") BigDecimal exchangeFee,
            @Param("externalCharges") BigDecimal externalCharges,
            @Param("performanceFee") BigDecimal performanceFee,
            @Param("stampDuty") BigDecimal stampDuty,
            @Param("positionId") String positionId,
            @Param("orderStatus") SaxoOrderStatusEnum orderStatus);

    List<SaxoOrderPO> getListByTime(@Param("nowDate") Date nowDate);

    List<SaxoOrderPO> getWaitNotifyFee(@Param("orderFeeStatus") SaxoOrderFeeStatusEnum orderFeeStatus);

    void confirmNotifyFee(@Param("orderId") Long orderId, @Param("orderFeeStatus") SaxoOrderFeeStatusEnum orderFeeStatus);

    void confirmOrderSuccess(@Param("orderId") Long orderId, @Param("saxoOrderCode") String saxoOrderCode, @Param("orderStatus") SaxoOrderStatusEnum orderStatus);

    List<SaxoOrderPO> findSaxoOrder(SaxoOrderPO saxoOrderPO);*/
}
