package com.pivot.aham.api.service.job.impl.rebalance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月22日
 */
@Data
public class ReBalanceTriggerResult {
    private Boolean ifAdj;
    private Long balTimeDiff;
    private BigDecimal etfDiff;
    private BigDecimal xValue;
    private Long adjt;

}
