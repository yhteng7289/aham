package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountBalanceHisRecordResDTO extends BaseDTO {
    private Long accountId;
    private Date lastBalTime;
    private Long balId;
    private String lastProductWeight;
    private BigDecimal portfolioScore;
}
