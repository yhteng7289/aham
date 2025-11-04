package com.pivot.aham.api.server.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UobExchangeResult implements Serializable{
    private Long orderId;

    // TODO: 2019-01-22 假数据，未来会删掉
    private BigDecimal confirmAmount;
}
