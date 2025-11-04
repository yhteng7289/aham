package com.pivot.aham.api.service.remote.impl.wrapperbean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Data
@Accessors(chain = true)
public class SquirrelsaveCashBean {

    private BigDecimal cashUsd;
    private BigDecimal cashSgd;

    private BigDecimal totalCashUsd;
    private BigDecimal totalCashSgd;

}
