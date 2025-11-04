package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
public class ExchangeRateDTO extends BaseDTO {
    private Date rateDate;
    private ExchangeRateTypeEnum exchangeRateType;

}
