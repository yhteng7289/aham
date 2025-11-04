package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.RechargeOrderStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
@Accessors
public class AccountRechargeResDTO extends BaseDTO {
    private Long accountId;
    private Date rechargeTime;
    private CurrencyEnum currency;
    private BigDecimal rechargeAmount;
    private String clientId;
    private RechargeOrderStatusEnum orderStatus;
    private Long rechargeOrderNo;
    private Long executeOrderNo;
    private String bankOrderNo;
    private String goalId;
    private TpcfStatusEnum tpcfStatus;
    private Date tpcfTime;


}
