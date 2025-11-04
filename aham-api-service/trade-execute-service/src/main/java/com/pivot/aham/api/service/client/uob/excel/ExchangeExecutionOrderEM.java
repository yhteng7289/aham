package com.pivot.aham.api.service.client.uob.excel;

import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import lombok.Data;

@Data
public class ExchangeExecutionOrderEM {

    @ExcelField(title = "ExecutionOrderId")
    private String executionOrderId;

    @ExcelField(title = "FromCurrency")
    private String fromCurrency;

    @ExcelField(title = "SpendingAmount")
    private String spendingAmount;

    @ExcelField(title = "ToCurrency")
    private String toCurrency;

    @ExcelField(title = "OrderTime")
    private String orderTime;

    @ExcelField(title = "remark")
    private String remark;
}
