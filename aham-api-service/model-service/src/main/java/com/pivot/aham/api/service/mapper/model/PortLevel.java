package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.RebalanceEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


@Data
@Accessors(chain = true)
public class PortLevel extends BaseModel {
    @TableField("model_date")
    private Date modelDate;
    @TableField("portfolio_level")
    private BigDecimal portfolioLevel;
    @TableField("max_dd")
    private BigDecimal maxDD;
    @TableField("vol")
    private BigDecimal vol;
    @TableField("return")
    private BigDecimal returnVol;
    @TableField("rebalance")
    private RebalanceEnum rebalance;
    @TableField("portfolio_id")
    private String portfolioId;
    @TableField("benchmark_data")
    private BigDecimal benchmarkData;
    @TableField("voo_ten_days")
    private Boolean vooTenDays;


}

