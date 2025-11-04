package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("ClientTransResVo-请求对象说明")
public class TotalFeeAccountExportResVO {

    @ExcelField(title = "money")
    private BigDecimal money;
    @ExcelField(title = "feeType")
    private String feeType;
    @ExcelField(title = "operateType")
    private String operateType;
    @ExcelField(title = "accountId")
    private Long accountId;
    @ExcelField(title = "operateType")
    private Date operateDate;
    @ExcelField(title = "goalId")
    private String goalId;
    @ExcelField(title = "clilentId")
    private Long clientId;

}
