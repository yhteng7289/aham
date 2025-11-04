package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
public class HoldingInstrumentResp {
    private Integer uic;
    private BigDecimal amount;
    private BigDecimal averageOpenPrice;
    private String netPositionId;

    public HoldingInstrumentResp(){}

    public HoldingInstrumentResp(Integer uic){
        this.uic = uic;
        this.amount = BigDecimal.ZERO;
        this.averageOpenPrice = BigDecimal.ZERO;
        this.netPositionId = StringUtils.EMPTY;

    }
}
