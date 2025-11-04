package com.pivot.aham.api.service.job.wrapperbean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by luyang.li on 18/12/21.
 */
@Data
@Accessors(chain = true)
public class BuyEtfMoneyWrapperBean {
    private BigDecimal totalBuyEtfMoney;
    private Map<String, BigDecimal> userBuyEtfMoneyMap;

}
