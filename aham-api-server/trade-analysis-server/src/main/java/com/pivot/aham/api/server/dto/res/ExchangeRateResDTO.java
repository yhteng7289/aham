package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
public class ExchangeRateResDTO extends BaseDTO {
    private BigDecimal usdToSgd;
    private Date rateDate;
    private ExchangeRateTypeEnum exchangeRateType;
    private Date createTime;
    private Date updateTime;
}
