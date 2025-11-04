package com.pivot.aham.api.service.job.wrapperbean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Data
@Accessors
public class SaxoSgdMoneyBean {

    private BigDecimal sgdRechargeMoney;
    private BigDecimal sgdRedeemMoney;

}
