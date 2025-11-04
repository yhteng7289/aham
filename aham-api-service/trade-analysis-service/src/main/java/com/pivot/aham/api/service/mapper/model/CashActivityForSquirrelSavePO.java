package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName(value = "t_cash_activity_for_squirrelsave",resultMap = "CashActivityForSquirrelSaveRes")
public class CashActivityForSquirrelSavePO extends BaseModel {
    private Long custStatementId;
    private String virtualAccountNo;
    private String activityDesc;
    private BigDecimal activityAmountSgd;
    private BigDecimal activityAmountUsd;
    private Date activityTime;

}
