package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.ExchangeOrderTypeEnum;
import com.pivot.aham.common.enums.ExchangeTypeEnum;
import com.pivot.aham.common.enums.UobOrderStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UobExchangeOrderPO {
    private Long id;
    private Long outBusinessId;
    private ExchangeOrderTypeEnum orderType;
    private ExchangeTypeEnum exchangeType;
    private UobOrderStatusEnum orderStatus;
    private BigDecimal applyAmount;
    private Date applyTime;
    private BigDecimal confirmAmount;
    private Date confirmTime;
    private BigDecimal costFee;
}
