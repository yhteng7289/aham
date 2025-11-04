package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UobTransferOrderPO;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.UobOrderStatusEnum;
import com.pivot.aham.common.enums.UobTransferOrderTypeEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface UobTransferOrderMapper extends BaseMapper {
    void save(UobTransferOrderPO uobTransferOrderPO);

    List<UobTransferOrderPO> getOrderList(
            @Param("typeList") List<UobTransferOrderTypeEnum> typeList,
            @Param("statusList") List<UobOrderStatusEnum> statusList);

    void updateStatus(@Param("orderId") Long orderId, @Param("status") UobOrderStatusEnum status);

    List<UobTransferOrderPO> getOrderListById(@Param("orderIdList") List<Long> orderIdList);

    void confirm(@Param("orderId") Long orderId, @Param("status") UobOrderStatusEnum status, @Param("cost") BigDecimal cost, @Param("confirmTime") Date confirmTime);

    void deleteLog(@Param("idList") List<Long> idList);
}