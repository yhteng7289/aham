package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.UobOrderRelationTypeEnum;
import lombok.Data;

@Data
public class UobTransferOrderRelationPO {
    private Long id;
    private Long businessOrderId;
    private Long executionOrderId;
    private UobOrderRelationTypeEnum relationType;
}
