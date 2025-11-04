package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.TransferStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * etf交易回调
 * @author addison
 * @since 2018年12月10日
 */
@Data
@Accessors(chain = true)
public class EtfCallbackDTO extends BaseVo {
    private Long tmpOrderId;
    private Long accountId;
    private String productCode;
    private BigDecimal confirmMoney;
    private BigDecimal confirmShare;
    private BigDecimal transCost;
    private TransferStatusEnum transferStatus;
    private Date confirmTime;
}
