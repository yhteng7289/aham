/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.FeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author HP
 */
public class TotalFeeAccountResDTO extends BaseDTO {

    private BigDecimal money;

    private FeeTypeEnum feeType;

    private OperateTypeEnum operateType;

    private Long accountId;

    private Date operateDate;

    private String goalId;

    private Long clientId;

    public BigDecimal getMoney() {
        return money.setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public FeeTypeEnum getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeTypeEnum feeType) {
        this.feeType = feeType;
    }

    public OperateTypeEnum getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateTypeEnum operateType) {
        this.operateType = operateType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

}
