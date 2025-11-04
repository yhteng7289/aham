package com.pivot.aham.api.service.bean;

import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by luyang.li on 19/3/21.
 */
@Data
public class RechargeRefundBean implements Serializable {
    @ExcelField(title = "ClientID")
    private String clientId;
    @ExcelField(title = "clientName")
    private String clientName;
    @ExcelField(title = "VirtualAccountNo")
    private String virtualAccountNo;
    @ExcelField(title = "BankOrderNumber")
    private String bankOrderNumber;
    @ExcelField(title = "BankProvidedName")
    private String bankProvidedName;
    @ExcelField(title = "Amount")
    private BigDecimal amount;
    @ExcelField(title = "Currency")
    private String currency;
    @ExcelField(title = "TradeTime")
    private String tradeTime;

}
