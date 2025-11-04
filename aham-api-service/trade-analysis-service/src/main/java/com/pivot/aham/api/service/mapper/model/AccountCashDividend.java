package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
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
@TableName(value = "t_account_cash_dividend",resultMap = "AccountCashDividendRes")
public class AccountCashDividend extends BaseModel{
    private Long account_id;
    private String client_id;
    private BigDecimal divCash;
}
