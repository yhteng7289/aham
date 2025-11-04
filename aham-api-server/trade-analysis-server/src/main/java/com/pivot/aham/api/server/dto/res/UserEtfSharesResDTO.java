package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserEtfSharesResDTO extends BaseDTO {
    private Date staticDate;
    private Long accountId;
    private String clientId;
    private String goalId;
    private String productCode;
    private BigDecimal shares;
    private BigDecimal money;
}
