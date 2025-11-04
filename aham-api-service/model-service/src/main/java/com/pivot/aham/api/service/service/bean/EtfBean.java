package com.pivot.aham.api.service.service.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/7.
 */
@Data
@Accessors(chain = true)
public class EtfBean {
    //
    private String etf;
    private BigDecimal weight;

}
