package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账户调仓历史记录
 *
 * @author addison
 * @since 2019年03月18日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_balance_his_record",resultMap = "AccountBalanceHisRecordRes")
public class AccountBalanceHisRecord extends BaseModel{
    private Long accountId;
    private Date lastBalTime;
    private Long balId;
    private String lastProductWeight;
    private BigDecimal portfolioScore;
}
