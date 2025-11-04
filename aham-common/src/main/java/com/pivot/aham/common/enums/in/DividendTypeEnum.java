package com.pivot.aham.common.enums.in;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 费率来源
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum DividendTypeEnum implements IEnum {
    CASH(0, "现金分红"),
    SHARE(1, "份额分红");

    private int value;
    private String desc;

    DividendTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static DividendTypeEnum forValue(Integer value) {
        for (DividendTypeEnum initDayEnum : DividendTypeEnum.values()) {
            if (Objects.equals(initDayEnum.getValue(), value)) {
                return initDayEnum;
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
