package com.pivot.aham.api.service.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/11.
 */
@Data
@Accessors(chain = true)
public class BankVABean {
    private String clientId;
    //可用金额
    private BigDecimal cashAmount = BigDecimal.ZERO;
    //冻结金额
    private BigDecimal freezeAmount = BigDecimal.ZERO;
    //'账户使用金额'
    private BigDecimal usedAmount = BigDecimal.ZERO;
    private Long id;
}
