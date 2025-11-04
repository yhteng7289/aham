package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.BalTradeTypeEnum;
import com.pivot.aham.common.enums.analysis.ExecuteStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountBalanceAdjDetailResDTO extends BaseDTO {
    private Long balId;
    private String productCode;
    private BigDecimal currentHold;
    private BigDecimal targetHold;
    private BalTradeTypeEnum tradeType;
    private ExecuteStatusEnum executeStatus;
    private Long tmpOrderId;
    private BigDecimal correctTargetHold;
    private BigDecimal tradeAmount;
}
