package com.pivot.aham.api.service.job.wrapperbean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors
public class AccountRedeemWrapperBean {

    //计算每个account下用户提现比例
    private BigDecimal accountCashWithdrawal = BigDecimal.ZERO;
    private BigDecimal totalApplyRedeem = BigDecimal.ZERO;
    private BigDecimal totalConfirmShares = BigDecimal.ZERO;

}