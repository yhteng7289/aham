package com.pivot.aham.api.service.job.wrapperbean;

import com.pivot.aham.common.enums.ProductMainSubTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/21.
 */
@Data
@Accessors(chain = true)
public class BuyEtfWrapperBean {
    private BigDecimal confirmMoney;
    private Long tmpTotalOrderNo;
    private Long accountId;
    private String productCode;
    private ProductMainSubTypeEnum productCodeType;
    private boolean hasFailEtf;

}
