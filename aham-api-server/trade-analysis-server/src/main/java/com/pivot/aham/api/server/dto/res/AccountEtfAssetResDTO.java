package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by luyang.li on 19/3/4.
 */
@Data
@Accessors(chain = true)
public class AccountEtfAssetResDTO extends BaseDTO{
    /**
     * key: product code
     * value: holding share
     */
    private Map<String, BigDecimal> dataMap;
}
