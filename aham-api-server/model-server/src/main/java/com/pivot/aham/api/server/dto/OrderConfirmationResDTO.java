package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.FirstClassfiyTypeEnum;
import com.pivot.aham.common.enums.ProductMainSubTypeEnum;
import com.pivot.aham.common.enums.ProductTradeStatusEnum;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class OrderConfirmationResDTO extends BaseDTO {
        
    private Long id;
    private String referenceID;
    private String investorID;
    private String scheme;
    private String plan;
    private String tranType;
    private Date tranDate;
    private Date valueDate;
    private String agent;
    private String currency;
    private BigDecimal amount;
    private BigDecimal nav;
    private BigDecimal units;
    private BigDecimal totalCharge;
    private BigDecimal salesChargePercent;
    private BigDecimal salesChargeValue;
}
