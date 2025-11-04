package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.EtfOrderPO;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.EtfOrderStatusEnum;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface EtfOrderMapper extends BaseMapper {

    void save(EtfOrderPO saxoOrderPO);

    List<EtfOrderPO> getListByStatus(@Param("status") EtfOrderStatusEnum status);

    List<EtfOrderPO> getListByStatusAndType(@Param("status") EtfOrderStatusEnum status, @Param("orderTypes") List<EtfOrderTypeEnum> orderType);

    List<EtfOrderPO> getListByMergeOrderId(@Param("mergeOrderId") Long mergeOrderId);

    void updateMergeOrderId(
            @Param("orderIdList") List<Long> orderIdList,
            @Param("mergeOrderId") Long mergeOrderId,
            @Param("status") EtfOrderStatusEnum status,
            @Param("price") BigDecimal price);

    void updateStatus(
            @Param("orderIdList") List<Long> orderIdList,
            @Param("status") EtfOrderStatusEnum status);

    void confirm(
            @Param("etfOrderId") Long etfOrderId,
            @Param("orderStatus") EtfOrderStatusEnum orderStatus,
            @Param("costFee") BigDecimal costFee,
            @Param("confirmTime") Date confirmTime,
            @Param("confirmShare") BigDecimal confirmShare,
            @Param("confirmAmount") BigDecimal confirmAmount,
            @Param("remark") String remark);

    void updateApplyShare(
            @Param("etfOrderId") Long etfOrderId,
            @Param("applyShare") BigDecimal applyShare);

    void confirms(
            @Param("etfOrderIds") List<Long> etfOrderIds,
            @Param("orderStatus") EtfOrderStatusEnum orderStatus,
            @Param("costFee") BigDecimal costFee,
            @Param("confirmTime") Date confirmTime,
            @Param("confirmShare") BigDecimal confirmShare,
            @Param("confirmAmount") BigDecimal confirmAmount);

    void notifySuccess(@Param("orderIdList") List<Long> orderIdList, @Param("status") EtfOrderStatusEnum status);

    void updatePrice(@Param("id") Long id, @Param("tradePrice") BigDecimal tradePrice);
}
