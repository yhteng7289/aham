package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 19/3/29.
 */
@Data
@Accessors(chain = true)
public class ExchangeRatePO extends BaseModel {
    private Long id;
    private BigDecimal usdToSgd;
    private Date rateDate;
    private ExchangeRateTypeEnum exchangeRateType;
    private Date createTime;
    private Date updateTime;

}
