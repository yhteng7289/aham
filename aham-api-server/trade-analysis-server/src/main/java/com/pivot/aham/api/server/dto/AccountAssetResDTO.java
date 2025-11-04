package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
public class AccountAssetResDTO extends BaseDTO {
    private String productCode;
    private Long accountId;
    private BigDecimal productShare;
    private BigDecimal productMoney;
    private ProductAssetStatusEnum productAssetStatus;
    private BigDecimal reconShareUnit;

}
