package com.pivot.aham.api.service.client.uob.excel;

import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import lombok.Data;

@Data
public class RechargeLogConfirmEM {

    @ExcelField(title = "BankOrderNumber")
    private String bankOrderNo;

    @ExcelField(title = "ClientName")
    private String clientName;

    @ExcelField(title = "AccountNumber")
    private String virtualAccountNo;

    @ExcelField(title = "Currency")
    private String currency;

    @ExcelField(title = "ReferenceCode")
    private String referenceCode;

    @ExcelField(title = "Amount")
    private String cashAmount;

    @ExcelField(title = "TradeTime")
    private String tradeTime;
    
    private String creditDebit;

    private String transactionInd;
}
