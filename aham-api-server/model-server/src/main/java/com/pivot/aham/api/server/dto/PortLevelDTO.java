package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.RebalanceEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
public class PortLevelDTO extends BaseDTO {

    private Date date;
    private BigDecimal portfolioLevel;
    private BigDecimal maxDD;
    private BigDecimal vol;
    private BigDecimal returnVol;
    private RebalanceEnum rebalance;
    private String portfolioId;
    private BigDecimal benchmarkData;

}
