package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum SaxoOrderTypeEnum implements IEnum {
    BUY(1, "买"),
    SELL(2, "卖"),
    ;

    private int value;
    private String desc;

    SaxoOrderTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static SaxoOrderTypeEnum forValue(Integer value) {
        for (SaxoOrderTypeEnum investTypeEnum : SaxoOrderTypeEnum.values()) {
            if (Objects.equals(investTypeEnum.getValue(), value)) {
                return investTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    public String getDesc() {
        return desc;
    }
}
