package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;


/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
public class AccountRechargeDTO extends BaseDTO {
    private BigDecimal tpcf;
    private List<String> clientId;
    private List<BigDecimal> rechargeAmount;

}
