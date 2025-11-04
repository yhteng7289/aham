package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.BalTradeTypeEnum;
import com.pivot.aham.common.enums.analysis.ExecuteStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 账户调仓方案明细
 *
 * @author addison
 * @since 2019年03月18日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_balance_adj_detail", resultMap = "AccountBalanceAdjDetailRes")
public class AccountBalanceAdjDetail extends BaseModel {

    private Long balId;
    private String productCode;
    private BigDecimal currentHold;
    private BigDecimal targetHold;
    private BalTradeTypeEnum tradeType;
    private ExecuteStatusEnum executeStatus;
    private Long tmpOrderId;
    private BigDecimal correctTargetHold;
    private BigDecimal tradeAmount;

}
