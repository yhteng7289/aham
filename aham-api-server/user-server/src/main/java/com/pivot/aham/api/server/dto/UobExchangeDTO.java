package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/3.
 */
@Data
@Accessors(chain = true)
public class UobExchangeDTO extends BaseDTO {

    //确认
    private BigDecimal confirmMoney;
    //ID　
    private Long virtualAccountOrderId;
}
