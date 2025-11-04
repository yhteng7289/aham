package com.pivot.aham.api.web.in.vo;


import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;

import java.util.Date;

@Data
public class BankOrderExportResVo {
    @ExcelField(title = "avaiableAmount")
    private String avaiableAmount;
    @ExcelField(title = "bankOrderNo")
    private String bankOrderNo;
    @ExcelField(title = "currency")
    private CurrencyEnum currency;
    @ExcelField(title = "referenceCode")
    private String referenceCode;
    @ExcelField(title = "typeDesc")
    private String typeDesc;
    @ExcelField(title = "matchStatusDesc")
    private String matchStatusDesc;
    @ExcelField(title = "virtualAccountNo")
    private String virtualAccountNo;
    @ExcelField(title = "tradeTime")
    private Date tradeTime;

}
