package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月04日
 */
@Data
@TableName(value = "t_saxo_to_uob_record_detail",resultMap = "SaxoToUobRecordDetailRes")
public class SaxoToUobRecordDetail extends BaseModel {
    private String saxoToUobBatchId;
    private String transactionId;
    private CurrencyEnum currency = CurrencyEnum.SGD;
    private BigDecimal amount;
    private Date confirmDate;
}
