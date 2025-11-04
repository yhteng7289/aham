package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月02日
 */
@Data
public class SaxoToUobOfflineConfirmByExcelDTO extends BaseDTO {

    @NotBlank(message = "batchId不能为空")
    @ExcelField(title = "batchId")
    private String saxoToUobBatchId;
    @ExcelField(title = "TrackingNo")
    private String transactionId;
    @ExcelField(title = "UOBRecievingAmouont")
    private BigDecimal amount;
    @ExcelField(title = "Date")
    private Date confirmDate;

}
