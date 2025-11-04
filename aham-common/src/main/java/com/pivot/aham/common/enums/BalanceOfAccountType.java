package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

public enum BalanceOfAccountType implements IEnum{
    CASH_BAL(1,"余额对账"),
    HOLD_BAL(2,"总持仓对账"),
    TOTAL_ASSETS_BAL(3,"总资产对账"),
    HOLD_PRODUCT_BAL(4,"ETF持仓对账");
    BalanceOfAccountType(Integer intValue, String desc){
        this.intValue=intValue;
        this.desc=desc;
    }
    private Integer intValue;
    private String desc;
    @Override
    public Integer getValue() {
        return intValue;
    }
    public String getDesc(){
        return desc;
    }
}
