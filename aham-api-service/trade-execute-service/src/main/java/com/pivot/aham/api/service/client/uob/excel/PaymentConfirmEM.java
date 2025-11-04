package com.pivot.aham.api.service.client.uob.excel;

import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import lombok.Data;

@Data
public class PaymentConfirmEM {
    @ExcelField(title = "ExecutionOrderId")
    private String executionOrderId;

    @ExcelField(title = "ConfirmCurrency")
    private String confirmCurrency;

    @ExcelField(title = "ConfirmAmount")
    private String confirmAmount;
}
