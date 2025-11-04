package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月04日
 */
@Data
@Accessors(chain = true)
@TableName(value="t_saxo_to_Uob_total_record",resultMap="SaxoToUobTotalRecorRes")
public class SaxoToUobTotalRecordPO extends BaseModel{
    /**
     * 批次id
     */
    @ExcelField(title = "batchId")
    private String saxoToUobBatchId;
    /**
     * 期望金额
     */
    @ExcelField(title = "transferAmount")
    private BigDecimal intendAmount;
    /**
     * 转账日期
     */
    @ExcelField(title = "transferDate")
    private Date transferDate;

    /**
     * 线下转账文件写死的提示字段，实际业务中没用到
     */
    @ExcelField(title = "Currency")
    private CurrencyEnum currency;
    @ExcelField(title = "From")
    private String form;
    @ExcelField(title = "to")
    private String to;

    /**
     * 已确认金额
     */
    private BigDecimal confirmedAmount;
    /**
     * 剩余金额
     */
    private BigDecimal residualAmount;
}
