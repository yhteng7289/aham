package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.math.BigDecimal;
import java.util.Objects;

public enum StabilityEnum implements IEnum {

    LEVEL1(1, "LEVEL1", new BigDecimal(10)),
    LEVEL2(2, "LEVEL2", new BigDecimal(6)),
    LEVEL3(3, "LEVEL3", new BigDecimal(2));

    private Integer value;
    private String desc;
    private BigDecimal score;

    StabilityEnum(Integer value, String desc, BigDecimal score) {
        this.value = value;
        this.desc = desc;
        this.score = score;
    }

    public static StabilityEnum forValue(Integer value) {
        for (StabilityEnum stabilityEnum : StabilityEnum.values()) {
            if (Objects.equals(stabilityEnum.getValue(), value)) {
                return stabilityEnum;
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

    public BigDecimal getScore() {
        return score;
    }
}
