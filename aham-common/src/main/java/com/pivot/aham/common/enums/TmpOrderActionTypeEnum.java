package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum TmpOrderActionTypeEnum implements IEnum {
    DEFAULT(-1, "默认值"),
    BUY(1, "买"),
    SELL(2, "卖"),;

    private Integer value;
    private String desc;

    TmpOrderActionTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TmpOrderActionTypeEnum forValue(Integer value) {
        for (TmpOrderActionTypeEnum tmpOrderActionTypeEnum : TmpOrderActionTypeEnum.values()) {
            if (Objects.equals(tmpOrderActionTypeEnum.getValue(), value)) {
                return tmpOrderActionTypeEnum;
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
