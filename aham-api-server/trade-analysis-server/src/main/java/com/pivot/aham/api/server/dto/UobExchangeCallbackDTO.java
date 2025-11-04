package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月10日
 */
@Data
@Accessors(chain = true)
public class UobExchangeCallbackDTO extends BaseVo {
    private Long orderNo;
    private BigDecimal confirmMoney;
}
