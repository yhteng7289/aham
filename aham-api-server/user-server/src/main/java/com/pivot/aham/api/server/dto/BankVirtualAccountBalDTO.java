package com.pivot.aham.api.server.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;

@Data
@Accessors(chain = true)
public class BankVirtualAccountBalDTO implements Serializable {

    @ExcelField(title="client_id")
    private String clientId;
    @ExcelField(title="client_name")
    private String clientName;
    /**
     * //虚拟账户
     */
    @ExcelField(title = "virtual_account_no")
    private String virtualAccountNo;
    /**
     * //可用金额
     */
    @ExcelField(title = "cash_amount")
    private BigDecimal cashAmount;
    /**
     * //冻结金额
     */
    @ExcelField(title = "freeze_amount")
    private BigDecimal freezeAmount;
    @ExcelField(title = "create_time")
    private String createTime;
    /**
     * //账户币种类型：1:美金账户,2:新币账户
     */
    @ExcelField(title = "currency")
    private String currency;
    /**
     * //'账户使用金额'
     */
    @ExcelField(title = "used_amount")
    private BigDecimal usedAmount;


}
