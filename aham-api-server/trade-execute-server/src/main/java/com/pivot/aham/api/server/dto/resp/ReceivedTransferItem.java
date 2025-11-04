package com.pivot.aham.api.server.dto.resp;

import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ReceivedTransferItem implements Serializable{
    private String bankOrderNo;
    private String clientName;
    private String virtualAccountNo;
    private CurrencyEnum currency;
    private String referenceCode;
    private BigDecimal cashAmount;
    private Date tradeTime;
    
    private Long id;
}
