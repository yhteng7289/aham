package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum SaxoOrderStatusEnum implements IEnum {
    TRADING(1, "交易中"),
    TRADING_SUCCESS(2, "交易成功"),
    FINISH(3, "交易确认成功"),
    ;

    private int value;
    private String desc;

    SaxoOrderStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static SaxoOrderStatusEnum forValue(Integer value) {
        for (SaxoOrderStatusEnum investTypeEnum : SaxoOrderStatusEnum.values()) {
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
