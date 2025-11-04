package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

public enum TradeType implements IEnum {
    BUY(1, "买"),
    SELL(2, "卖"),
    ;


    private int value;
    private String desc;


    TradeType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
