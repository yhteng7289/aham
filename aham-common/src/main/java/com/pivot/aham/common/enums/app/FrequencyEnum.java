package com.pivot.aham.common.enums.app;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author YYYz
 */
public enum FrequencyEnum implements IEnum {

    lumpsum(1, "Lumpsum"),
    yearly(2, "Yearly"),
    monthly(3, "Monthly");

    FrequencyEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static FrequencyEnum forValue(Integer value) {
        for (FrequencyEnum frequencyEnum : FrequencyEnum.values()) {
            if (Objects.equals(frequencyEnum.getValue(), value)) {
                return frequencyEnum;
            }
        }
        return null;
    }

    public static FrequencyEnum forDesc(String desc) {
        for (FrequencyEnum frequencyEnum : FrequencyEnum.values()) {
            if (Objects.equals(frequencyEnum.getDesc(), desc)) {
                return frequencyEnum;
            }
        }
        return null;
    }

    private Integer value;
    private String desc;

    @Override
    public Serializable getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
