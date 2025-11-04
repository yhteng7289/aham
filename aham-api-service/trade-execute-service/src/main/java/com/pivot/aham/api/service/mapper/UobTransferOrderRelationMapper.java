package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UobTransferOrderRelationPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UobTransferOrderRelationMapper extends BaseMapper {
    void save(UobTransferOrderRelationPO uobTransferOrderRelationPO);

    List<UobTransferOrderRelationPO> getByBusinessOrderId(@Param("businessOrderId") Long businessOrderId);

    List<UobTransferOrderRelationPO> getByExecutionOrderId(@Param("executionOrderId") Long executionOrderId);

    List<Long> getBusinessOrderIdByExecutionOrderId(@Param("executionOrderId") List<Long> executionIdList);

}