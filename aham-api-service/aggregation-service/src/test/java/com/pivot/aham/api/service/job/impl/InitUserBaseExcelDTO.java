package com.pivot.aham.api.service.job.impl;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月02日
 */
@Data
public class InitUserBaseExcelDTO extends BaseDTO {

    @ExcelField(title = "Client ID")
    private String clientId;
    @ExcelField(title = "Client Name")
    private String clientName;
    @ExcelField(title = "Virtual Account No SGD")
    private String virtualAccountNoSGD;
    @ExcelField(title = "Virtual Account No USD")
    private String virtualAccountNoUSD;
    @ExcelField(title = "Goal ID")
    private String goalId;
    @ExcelField(title = "Reference Code")
    private String referenceCode;
    @ExcelField(title = "Portfolio ID")
    private String portfolioID;

}
