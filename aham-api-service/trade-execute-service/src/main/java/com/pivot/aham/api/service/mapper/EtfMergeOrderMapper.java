package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.EtfMergeOrderPO;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.EtfMergeOrderStatusEnum;
import com.pivot.aham.common.enums.EtfmergeOrderTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface EtfMergeOrderMapper extends BaseMapper {

    void save(EtfMergeOrderPO etfMergeOrderPO);

    List<EtfMergeOrderPO> getMergeOrder(@Param("orderTypeList") List<EtfmergeOrderTypeEnum> orderTypeList, @Param("orderStatus") EtfMergeOrderStatusEnum orderStatus);

    EtfMergeOrderPO getBySaxoOrderId(@Param("saxoOrderId") Long saxoOrderId);

    EtfMergeOrderPO getById(@Param("mergeOrderId") Long mergeOrderId);

    void tradeExecute(
            @Param("mergeOrderId") Long mergeOrderId,
            @Param("orderStatus") EtfMergeOrderStatusEnum orderStatus,
            @Param("saxoOrderId") Long saxoOrderId,
            @Param("totalSellAmount") BigDecimal totalSellAmount,
            @Param("totalBuyAmount") BigDecimal totalBuyAmount,
            @Param("totalSellShare") BigDecimal totalSellShare,
            @Param("totalBuyShare") BigDecimal totalBuyShare);

    void tradeExecutes(
            @Param("mergeOrderId") List<Long> mergeOrderIds,
            @Param("orderStatus") EtfMergeOrderStatusEnum orderStatus,
            @Param("saxoOrderId") Long saxoOrderId);

    void tradeConfirm(
            @Param("mergeOrderId") Long mergeOrderId,
            @Param("orderStatus") EtfMergeOrderStatusEnum orderStatus,
            @Param("costFee") BigDecimal costFee,
            @Param("confirmTime") Date confirmTime,
            @Param("confirmAmount") BigDecimal confirmAmount,
            @Param("confirmShare") BigDecimal confirmShare,
            @Param("totalSellShare") BigDecimal totalSellShare,
            @Param("totalBuyShare") BigDecimal totalBuyShare,
            @Param("crossedShare") BigDecimal crossedShare);

    void tradeConfirmsSameParam(
            @Param("mergeOrderIds") List<Long> mergeOrderIds,
            @Param("orderStatus") EtfMergeOrderStatusEnum orderStatus,
            @Param("costFee") BigDecimal costFee,
            @Param("confirmTime") Date confirmTime,
            @Param("confirmAmount") BigDecimal confirmAmount,
            @Param("confirmShare") BigDecimal confirmShare);

    void updateStatus(@Param("mergeOrderId") Long mergeOrderId, @Param("orderStatus") EtfMergeOrderStatusEnum orderStatus);

    void updateStatusAndSetPrice(@Param("mergeOrderId") Long mergeOrderId, @Param("orderStatus") EtfMergeOrderStatusEnum orderStatus,
            @Param("tradePrice") BigDecimal tradePrice);

}
