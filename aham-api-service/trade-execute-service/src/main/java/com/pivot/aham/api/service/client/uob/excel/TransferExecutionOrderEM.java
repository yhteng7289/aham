package com.pivot.aham.api.service.client.uob.excel;

import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import lombok.Data;

@Data
public class TransferExecutionOrderEM {

    @ExcelField(title = "ExecutionOrderId")
    private String executionOrderId;

    @ExcelField(title = "BankName")
    private String bankName;

    @ExcelField(title = "BankAccountNumber")
    private String bankAccountNumber;

    @ExcelField(title = "BankUserName")
    private String bankUserName;

    @ExcelField(title = "BranchCode")
    private String branchCode;

    @ExcelField(title = "SwiftCode")
    private String swiftCode;

    @ExcelField(title = "Currency")
    private String currency;

    @ExcelField(title = "Amount")
    private String amount;

    @ExcelField(title = "OrderTime")
    private String orderTime;

    @ExcelField(title = "remark")
    private String remark;
}
