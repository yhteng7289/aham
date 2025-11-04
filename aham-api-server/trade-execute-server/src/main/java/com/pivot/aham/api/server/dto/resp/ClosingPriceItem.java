package com.pivot.aham.api.server.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by hao.tong on 2018/12/24.
 */
@Data
public class ClosingPriceItem implements Serializable{
    private String etfCode;
    private BigDecimal price;
}
