package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 19/2/24.
 */
@Data
@Accessors(chain = true)
public class PortFutureLevelResDTO extends BaseDTO {

    private Date date;
    private BigDecimal rcmd;
    private BigDecimal sixtyEightLow;
    private BigDecimal sixtyEightUp;
    private BigDecimal ninetyFiveLow;
    private BigDecimal ninetyFiveUp;
    private String portfolioId;

}
