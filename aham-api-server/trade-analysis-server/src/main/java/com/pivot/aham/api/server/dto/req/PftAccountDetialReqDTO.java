package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.PftAssetOperateTypeEnum;
import com.pivot.aham.common.enums.analysis.PftAssetSourceEnum;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class PftAccountDetialReqDTO extends BaseDTO {
    private String productCode;
    private PftAssetSourceEnum pftAssetSource;
    private Long executeOrderNo;
    private PftAssetOperateTypeEnum pftAssetStatus;
    private BigDecimal confirmMoney;
    private BigDecimal confirmShare;

}
