package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import com.pivot.aham.common.enums.in.TransStatusEnum;
import com.pivot.aham.common.enums.in.TransTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("RoundingAccountExportResVO-请求对象说明")
public class RoundingAccountExportResVO {

    @ExcelField(title = "clientId")
    private String clientId;
    @ExcelField(title = "goalId")
    private String goalId;
    @ExcelField(title = "referenceCode")
    private String referenceCode;
    @ExcelField(title = "模型id")
    private String portfolioId;
    @ExcelField(title = "交易单号")
    private String transNo;
    @ExcelField(title = "交易时间")
    private Date transTime;
    @ExcelField(title = "交易类型")
    private TransTypeEnum transType;
    @ExcelField(title = "交易金额USD")
    private BigDecimal amountUsd;
    @ExcelField(title = "交易金额SGD")
    private BigDecimal amountSgd;
    @ExcelField(title = "交易状态")
    private TransStatusEnum transStatus;

}
