package com.pivot.aham.api.server.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hao.tong on 2018/12/24.
 */
@Data
public class ExchangeRateResult  implements Serializable {
    private String bsnDt;
    private BigDecimal USD_TO_SGD;
}
