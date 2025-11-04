package com.pivot.aham.api.service.mapper.model;


import com.pivot.aham.common.enums.SyncStatus;
import com.pivot.aham.common.enums.TradeType;
import com.pivot.aham.common.enums.analysis.PftAssetSourceEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * etf记账
 */

@Data
@Accessors(chain = true)
public class EtfMergePftOrderPO {
    private Long id;
    private Long mergeOrderId;
    private SyncStatus syncStatus;
    private String productCode;
    private BigDecimal amount;
    private BigDecimal share;
    private BigDecimal cost;
    private TradeType tradeType;
    private PftAssetSourceEnum sourceType;
    private Date createTime;
}
