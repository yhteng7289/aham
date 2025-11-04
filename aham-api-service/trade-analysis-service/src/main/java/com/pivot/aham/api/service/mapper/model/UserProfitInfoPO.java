package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 18/12/9.
 *
 * 用户资产统计
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_profit_info",resultMap = "UserProfitInfoRes")
public class UserProfitInfoPO extends BaseModel {
    @TableField("client_id")
    private String clientId;
    @TableField("account_id")
    private Long accountId;
    @TableField("goal_id")
    private String goalId;
    @TableField("total_profit")
    private BigDecimal totalProfit;
    @TableField("portfolio_profit")
    private BigDecimal portfolioProfit;
    @TableField("fx_impact")
    private BigDecimal fxImpact;
    @TableField("profit_date")
    private Date profitDate;

}
