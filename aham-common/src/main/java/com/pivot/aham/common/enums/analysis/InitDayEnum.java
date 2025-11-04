package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum InitDayEnum implements IEnum {
    INIT_DAY(1, "首单"),
    UN_INIT_DAY(2, "非首单");

    private int value;
    private String desc;

    InitDayEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static InitDayEnum forValue(Integer value) {
        for (InitDayEnum initDayEnum : InitDayEnum.values()) {
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
