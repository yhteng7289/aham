package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.UobOrderStatusEnum;
import com.pivot.aham.common.enums.UobTransferOrderTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UobTransferOrderPO {
    private Long id;
    private Long outBusinessId;
    private String bankName;
    private String bankAccountNumber;
    private String bankUserName;
    private String branchCode;
    private String swiftCode;
    private UobTransferOrderTypeEnum orderType;
    private UobOrderStatusEnum orderStatus;
    private CurrencyEnum currency;
    private BigDecimal amount;
    private BigDecimal costFee;
    private Date applyTime;
    private Date confirmTime;
    private String remark;
}
