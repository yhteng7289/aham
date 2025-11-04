package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * tpcf和tncf每日中间统计
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_goal_cash_flow",resultMap = "UserGoalCashFlowRes")
public class UserGoalCashFlowPO extends BaseModel {
    private Long accountId;
    private String clientId;
    private String goalId;
    private BigDecimal tpcf;
    private BigDecimal tncf;
    private Date staticDate;

    private Date startStaticDate;
    private Date endStaticDate;

}
