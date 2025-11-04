package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.EtfMergeOrderStatusEnum;
import com.pivot.aham.common.enums.EtfmergeOrderTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * etf合并后的订单
 */
@Data
@Accessors(chain = true)
public class EtfMergeOrderPO {

    private Long id;
    private EtfmergeOrderTypeEnum orderType;
    private EtfMergeOrderStatusEnum orderStatus;
    private String productCode;
    private BigDecimal costFee;
    private Date applyTime;
    private Date confirmTime;
    private BigDecimal applyAmount;
    private BigDecimal confirmAmount;
    private BigDecimal confirmShare;
    private Long saxoOrderId;
    private BigDecimal tradePrice;
    private BigDecimal totalSellShare;
    private BigDecimal totalBuyShare;
    private BigDecimal totalSellAmount;
    private BigDecimal totalBuyAmount;
    private BigDecimal crossedShare;
}
