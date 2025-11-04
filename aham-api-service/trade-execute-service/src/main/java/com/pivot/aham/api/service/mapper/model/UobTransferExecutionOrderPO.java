package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.UobOrderRelationTypeEnum;
import com.pivot.aham.common.enums.UobExecutionOrderStatusEnum;
import com.pivot.aham.common.enums.UobTransferOrderTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UobTransferExecutionOrderPO {
    private Long id;
    private String bankName;
    private String bankAccountNumber;
    private String bankUserName;
    private String branchCode;
    private String swiftCode;
    private UobTransferOrderTypeEnum orderType;
    private UobExecutionOrderStatusEnum orderStatus;
    private CurrencyEnum currency;
    private BigDecimal amount;
    private BigDecimal costFee;
    private Date applyTime;
    private Date confirmTime;
    private UobOrderRelationTypeEnum relationType;
    private String remark;
}
