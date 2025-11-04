package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 账户基金信息
 *
 * @author addison
 * @since 2018年12月13日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_fund_nav",resultMap = "AccountFundNavRes")
public class AccountFundNavPO extends BaseModel {
    private Long accountId;
    private BigDecimal fundNav;
    private Date navTime;
    private BigDecimal totalShare;
    private BigDecimal totalAsset;
    private BigDecimal totalCash;

}
