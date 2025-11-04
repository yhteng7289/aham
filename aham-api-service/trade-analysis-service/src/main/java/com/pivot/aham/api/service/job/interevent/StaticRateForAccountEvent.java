package com.pivot.aham.api.service.job.interevent;

import com.pivot.aham.common.enums.analysis.FxRateTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
@Data
public class StaticRateForAccountEvent {
    private Long accountId;
    private BigDecimal fxRate;
    private FxRateTypeEnum fxRateTypeEnum;

}
