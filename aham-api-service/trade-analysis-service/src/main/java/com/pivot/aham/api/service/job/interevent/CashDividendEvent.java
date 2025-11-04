package com.pivot.aham.api.service.job.interevent;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月06日
 */
@Data
@Accessors(chain = true)
public class CashDividendEvent {
    private Long accountId;
    /**
     * 现金分红
     */
    private BigDecimal cashDividend;

}
