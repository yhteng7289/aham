package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import com.pivot.aham.common.enums.TmpOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.TmpOrderExecuteStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TmpOrderRecordResDTO  extends BaseDTO {
    private Long accountId;
    private Long tmpOrderId;
    private Long totalTmpOrderId;
    private Long executeOrderId;
    private String productCode;
    private BigDecimal applyMoney;
    private BigDecimal confirmMoney;
    private BigDecimal confirmTradeShares;
    private TmpOrderActionTypeEnum actionType;
    private TmpOrderExecuteStatusEnum tmpOrderTradeStatus;
    private EtfOrderTypeEnum tmpOrderTradeType;
    private Date applyTime;
    private Date confirmTime;
    private BigDecimal transCost;
}
