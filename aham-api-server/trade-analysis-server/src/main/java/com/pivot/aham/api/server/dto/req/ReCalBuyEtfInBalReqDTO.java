package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.TransferStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月24日
 */
@Data
public class ReCalBuyEtfInBalReqDTO  extends BaseDTO {
    private BigDecimal confirmPrice;
    private Long tmpOrderId;
    private Long accountId;
    private String productCode;
    private BigDecimal confirmMoney;
    private BigDecimal confirmShare;
    private TransferStatusEnum transferStatus;

}
