package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UobTransferExecutionOrderPO;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.UobExecutionOrderStatusEnum;
import com.pivot.aham.common.enums.UobTransferOrderTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface UobTransferExecutionOrderMapper extends BaseMapper {
    UobTransferExecutionOrderPO getById(@Param("id") Long id);
    void save(UobTransferExecutionOrderPO uobTransferExecutionOrderPO);

    List<UobTransferExecutionOrderPO> getOrderList(
            @Param("orderType") UobTransferOrderTypeEnum orderType,
            @Param("statusList") List<UobExecutionOrderStatusEnum> statusList);
    void confirm(
            @Param("id") Long id,
            @Param("status") UobExecutionOrderStatusEnum status,
            @Param("cost") BigDecimal cost,
            @Param("confirmTime") Date confirmTime);

    void updateStatus(@Param("id") Long id,
                      @Param("status") UobExecutionOrderStatusEnum status);

    List<UobTransferExecutionOrderPO> getByIdList(@Param("idList") List<Long> idList);

}