package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UobTransferReq implements Serializable{
    private Long outBusinessId;
    private CurrencyEnum currency;
    private BigDecimal amount;
    private String remark;

    /**
     * 以下:
     * 提现到银行卡的情况必填
     * 转账到SAXO不填
     */
    private String bankName;
    private String bankAccountNumber;
    private String bankUserName;
    private String bankAddress;

    /**
     * 以下海外银行必填
     */
    private String branchCode;
    private String swiftCode;
}
