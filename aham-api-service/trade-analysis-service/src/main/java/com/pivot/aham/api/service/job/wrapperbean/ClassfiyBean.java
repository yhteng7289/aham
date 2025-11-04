package com.pivot.aham.api.service.job.wrapperbean;

import com.pivot.aham.common.enums.FirstClassfiyTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/7.
 */
@Data
@Accessors(chain = true)
public class ClassfiyBean {
    private FirstClassfiyTypeEnum classfiyName;
    private BigDecimal percentage;

}
