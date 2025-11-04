package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 19/3/5.
 */
@Data
@Accessors(chain = true)
public class ClassfiyEtfWrapper extends BaseDTO {
    private String classfiyName;
    private BigDecimal percentage;
    private List<ProductWeight> etfPercentageVoList;

}
