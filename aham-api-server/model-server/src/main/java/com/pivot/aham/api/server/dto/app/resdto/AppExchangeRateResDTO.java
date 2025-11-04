package com.pivot.aham.api.server.dto.app.resdto;

import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class AppExchangeRateResDTO {

    private BigDecimal usdToSgd;
    private Date rateDate;
    private ExchangeRateTypeEnum exchangeRateType;
    private Date createTime;
    private Date updateTime;
}
