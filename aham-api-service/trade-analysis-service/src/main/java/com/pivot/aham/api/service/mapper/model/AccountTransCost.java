package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.TransCostSourceEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_trans_cost",resultMap = "AccountTransCostRes")
public class AccountTransCost extends BaseModel{
    private Long accountId;
    private String clientId;
    private BigDecimal transCost;
    private String productCode;
    private TransCostSourceEnum transCostSource;
    private Long tmpOrderId;
}
