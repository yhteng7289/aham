package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.enums.ExchangeTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UobExchangeReq implements Serializable{
    private Long outBusinessId;
    private BigDecimal exchangeAmount;
    private ExchangeTypeEnum exchangeType;
}
