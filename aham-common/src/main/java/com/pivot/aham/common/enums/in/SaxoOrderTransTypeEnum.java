package com.pivot.aham.common.enums.in;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum SaxoOrderTransTypeEnum implements IEnum {
    BUY(0, "买"),
    SELL(1, "卖")

    ;

    private Integer value;
    private String desc;

    SaxoOrderTransTypeEnum(Integer value, String desc ) {
        this.value = value;
        this.desc = desc;
    }

    public static SaxoOrderTransTypeEnum forValue(Integer value) {
        for (SaxoOrderTransTypeEnum ageEnum : SaxoOrderTransTypeEnum.values()) {
            if (Objects.equals(ageEnum.getValue(), value)) {
                return ageEnum;
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
