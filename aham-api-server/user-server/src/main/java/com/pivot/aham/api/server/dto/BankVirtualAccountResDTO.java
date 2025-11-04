package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/3.
 */
@Data
@Accessors(chain = true)
public class BankVirtualAccountResDTO extends BaseDTO {
    private String clientId;
    private String clientName;
    private String virtualAccountNo;
    private CurrencyEnum currency;
    //可用金额
    private BigDecimal cashAmount;
    //冻结金额
    private BigDecimal freezeAmount;
    //'账户使用金额'
    private BigDecimal usedAmount;
}
