package com.pivot.aham.api.service.job.wrapperbean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Data
public class RechargeCallbackExchangeBean {

    /**
     *  充值回调，成功状态
     */
    private boolean exchangeSuccess = false;
    /**
     * 充值回调，确认金额
     */
    private BigDecimal confirmMoney = BigDecimal.ZERO;
}
