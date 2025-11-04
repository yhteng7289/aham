package com.pivot.aham.api.service.client.rest.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ModelPortfolioDetailResp {

    private int id;
    private String name;
    private String scheme;
    private String productCode;
    private BigDecimal weightage;
    private String validFrom;
    private String validTo;
    private BigDecimal score;

}
