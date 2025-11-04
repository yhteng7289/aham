package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.EtfOrderStatusEnum;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * etf原始订单
 *
 * @author addison
 * @since 2018年12月08日
 */
@Data
@Accessors(chain = true)
public class EtfOrderPO {

    private Long id;
    private Long accountId;
    private EtfOrderTypeEnum orderType;
    private EtfOrderStatusEnum orderStatus;
    private String productCode;
    private BigDecimal costFee;
    private Date applyTime;
    private Date confirmTime;
    private BigDecimal applyAmount;
    private BigDecimal tmpApplyAmount;
    private BigDecimal applyShare;
    private BigDecimal confirmAmount;
    private BigDecimal confirmShare;
    private Long outBusinessId;
    private Long mergeOrderId;
    private BigDecimal tradePrice;

}
