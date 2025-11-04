package com.pivot.aham.api.service.client.rest.resp;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class ProductClosingPriceResp {
    private String fundCode;
    private String fundName;
    private String fundType;
    private String schemeCode;
    private String className;
    private String currency;
    private Date navDate;
    private BigDecimal navPrice;

}
