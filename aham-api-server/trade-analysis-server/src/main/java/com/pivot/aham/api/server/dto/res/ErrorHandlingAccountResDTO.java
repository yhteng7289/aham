/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.ErrorFeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import java.math.BigDecimal;

/**
 *
 * @author HP
 */
public class ErrorHandlingAccountResDTO extends BaseDTO {

    private BigDecimal money;

    private OperateTypeEnum operateType;

    private ErrorFeeTypeEnum type;

    private String transNo;

    public BigDecimal getMoney() {
        return money.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public OperateTypeEnum getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateTypeEnum operateType) {
        this.operateType = operateType;
    }

    public ErrorFeeTypeEnum getType() {
        return type;
    }

    public void setType(ErrorFeeTypeEnum type) {
        this.type = type;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

}
