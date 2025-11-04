package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum DateTypeEnum implements IEnum {
    HOLIDAY(0, "节假日"),
    NOTHOLIDAY(1, "非节假日");

    private int value;
    private String desc;

    DateTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static DateTypeEnum forValue(Integer value) {
        for (DateTypeEnum investTypeEnum : DateTypeEnum.values()) {
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
