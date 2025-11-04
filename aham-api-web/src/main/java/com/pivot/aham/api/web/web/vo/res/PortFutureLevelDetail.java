package com.pivot.aham.api.web.web.vo.res;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 19/2/26.
 */
@Data
@Accessors(chain = true)
public class PortFutureLevelDetail {

    private Date date;
    private BigDecimal rcmd;
    private BigDecimal sixtyEightLow;
    private BigDecimal sixtyEightUp;
    private BigDecimal ninetyFiveLow;
    private BigDecimal ninetyFiveUp;
    private String portfolioId;

}
