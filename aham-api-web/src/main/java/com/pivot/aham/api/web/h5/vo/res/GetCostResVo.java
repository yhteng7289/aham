package com.pivot.aham.api.web.h5.vo.res;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class GetCostResVo {

    private BigDecimal cost;
}
