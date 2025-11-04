package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum UobOrderRelationTypeEnum implements IEnum {
    MERGE(1, "合并"),
    SPLIT(2, "拆分"),
    EQUAL(3, "相等"),
    ;

    private int value;
    private String desc;

    UobOrderRelationTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UobOrderRelationTypeEnum forValue(Integer value) {
        for (UobOrderRelationTypeEnum investTypeEnum : UobOrderRelationTypeEnum.values()) {
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
