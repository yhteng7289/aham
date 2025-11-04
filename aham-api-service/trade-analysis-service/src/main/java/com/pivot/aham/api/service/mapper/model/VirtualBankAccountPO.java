package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by dexter on 11/4/2020
 *
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_bank_virtual_account",resultMap = "bankVirtualAccountMap")
public class VirtualBankAccountPO extends BaseModel {
    private int clientId;
    
    private String virtualAccountNo;
    
    private BigDecimal cashAmount;
    
    private BigDecimal freezeAmount;

    private BigDecimal usedAmount;
    
    private CurrencyEnum currency;

    private String clientName;



}
