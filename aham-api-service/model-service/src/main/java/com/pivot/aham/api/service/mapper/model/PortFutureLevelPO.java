package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.PortFutureStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PortFutureLevelPO extends BaseModel {
    @TableField("model_date")
    private Date modelDate;
    @TableField("rcmd")
    private BigDecimal rcmd;
    @TableField("sixty_eight_low")
    private BigDecimal sixtyEightLow;
    @TableField("sixty_eight_up")
    private BigDecimal sixtyEightUp;
    @TableField("ninety_five_low")
    private BigDecimal ninetyFiveLow;
    @TableField("ninety_five_up")
    private BigDecimal ninetyFiveUp;
    @TableField("status")
    private PortFutureStatusEnum status;
    @TableField("portfolio_id")
    private String portfolioId;


}

