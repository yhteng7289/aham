package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by hao.tong on 2018/12/25.
 */
@Data
public class InterAccountTransferResp {
    private BigDecimal commission;
    private BigDecimal fromAccountAmount;
    private String fromAccountCurrency;
    private BigDecimal toAccountAmount;
    private String toAccountCurrency;
}
