package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.util.Date;

@Data
public class AccountDividendReqDTO extends BaseDTO {
    private Long accountId;
    private String productCode;

    private Integer pageNo;
    private Integer pageSize;

    private Date tradeStartDate;
    private Date tradeEndDate;

    //查询辅助
    private String likeProductCode;

}
