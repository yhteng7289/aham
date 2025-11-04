package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 费率来源
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum FxRateTypeEnum implements IEnum {
    FUNDIN(1, "入金"),
    FUNDOUT(2, "卖单"),
    CLEARING(3,"日终收盘");

    private int value;
    private String desc;

    FxRateTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static FxRateTypeEnum forValue(Integer value) {
        for (FxRateTypeEnum initDayEnum : FxRateTypeEnum.values()) {
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
