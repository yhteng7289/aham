package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class AccountBalanceHisRecordReqDTO extends BaseDTO {
    
    private Long accountId;
    private Date lastBalTime;
    private Long balId;
    private String lastProductWeight;
    private BigDecimal portfolioScore;
    
    private Integer pageNo;
    private Integer pageSize;
}

