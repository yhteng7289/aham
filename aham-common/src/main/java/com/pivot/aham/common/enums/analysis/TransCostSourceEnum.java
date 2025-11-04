package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 手续费来源
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum TransCostSourceEnum implements IEnum {
    DEFAULT(-1, "默认值"),
    BUY(1, "买单"),
    SELL(2, "卖单");

    private int value;
    private String desc;

    TransCostSourceEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TransCostSourceEnum forValue(Integer value) {
        for (TransCostSourceEnum initDayEnum : TransCostSourceEnum.values()) {
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
