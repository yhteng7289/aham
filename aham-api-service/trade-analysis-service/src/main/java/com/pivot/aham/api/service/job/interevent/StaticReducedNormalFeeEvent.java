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
public class StaticReducedNormalFeeEvent {
    private Long accountId;
    /**
     * 管理费,t-1日
     */
    private BigDecimal mgtFee;
    private BigDecimal custFee;
    private BigDecimal gstMgtFee;
    /**
     * t日
     */
    private BigDecimal perfFee;
    private BigDecimal gstPerFee;
}
