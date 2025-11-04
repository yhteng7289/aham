package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * Name Alias Status
 *
 * @author WooiTatt
 */
public enum NameAliasEnum implements IEnum {
    PENDING(0, "PENDING"),
    APPROVE(1, "APPROVE"),
    REJECTED(2, "REJECTED");

    private int value;
    private String desc;

    NameAliasEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static NameAliasEnum forValue(Integer value) {
        for (NameAliasEnum investTypeEnum : NameAliasEnum.values()) {
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
