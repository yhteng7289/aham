package com.pivot.aham.api.server.dto.resp;

import com.pivot.aham.common.enums.SaxoOrderTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class SaxoStatisShareTradesDTO implements Serializable{
    //订单号
    private Long orderNumber;
    //交易份额
    private BigDecimal tradeShares;
    //手续费
    private BigDecimal commission;
    //买卖
    private SaxoOrderTypeEnum orderType;
}
