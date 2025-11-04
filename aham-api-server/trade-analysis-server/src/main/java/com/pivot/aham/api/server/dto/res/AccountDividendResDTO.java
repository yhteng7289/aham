package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.DividendHandelStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountDividendResDTO extends BaseDTO {
    private Long accountId;
    /**
     * 除息日
     */
    private Date exDate;

    /**
     * 价值日期
     */
    private Date tradeDate;
    /**
     * 分红类型id
     */
    private Integer caEventTypeID;
    /**
     * 分红类型名称
     */
    private String caEventTypeName;
    /**
     * 入账额(税后金额)
     */
    private BigDecimal dividendAmount;
    private BigDecimal navDividendAmount;
    private DividendHandelStatusEnum handelStatus;
    private String productCode;
    private String dividendOrderId;
}
