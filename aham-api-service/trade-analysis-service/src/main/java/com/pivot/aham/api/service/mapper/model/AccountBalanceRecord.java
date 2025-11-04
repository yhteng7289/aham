package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.BalStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 账户调仓记录流水
 *
 * @author addison
 * @since 2019年03月18日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_balance_record",resultMap = "AccountBalanceRecordRes")
public class AccountBalanceRecord extends BaseModel{
    private Long accountId;
    private Date balStartTime;
    private Long modelRecommendId;
    private String portfolioId;
    private BigDecimal portfolioScore;
    private Long balTimeDiff;
    private BigDecimal etfDiff;
    private BigDecimal xValue;
    private BalStatusEnum balStatus;


    private List<BalStatusEnum> balStatusList;

    private Date startBalStartTime;
    private Date endBalStartTime;
}
