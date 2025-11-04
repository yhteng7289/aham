package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UobExchangeOrderPO;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.ExchangeOrderTypeEnum;
import com.pivot.aham.common.enums.UobOrderStatusEnum;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface UobExchangeOrderMapper extends BaseMapper {

    UobExchangeOrderPO getById(@Param("orderId") Long orderId);

    List<UobExchangeOrderPO> getOrderList(@Param("orderType") ExchangeOrderTypeEnum orderType,
            @Param("statusList") List<UobOrderStatusEnum> statusList);

    void save(UobExchangeOrderPO uobExchangeOrderPO);

    void updateStatus(@Param("orderId") Long orderId, @Param("status") UobOrderStatusEnum status);

    void confirm(@Param("id") Long id, @Param("status") UobOrderStatusEnum status,
            @Param("confirmAmount") BigDecimal confirmAmount, @Param("cost") BigDecimal cost,
            @Param("confirmTime") Date confirmTime);
}
