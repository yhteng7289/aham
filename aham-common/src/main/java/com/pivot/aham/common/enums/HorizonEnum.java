package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.math.BigDecimal;
import java.util.Objects;

public enum HorizonEnum implements IEnum {

    LEVEL1(1, "LEVEL1", new BigDecimal(2)),
    LEVEL2(2, "LEVEL2", new BigDecimal(6)),
    LEVEL3(3, "LEVEL3", new BigDecimal(10));

    private Integer value;
    private String desc;
    private BigDecimal score;

    HorizonEnum(Integer value, String desc, BigDecimal score) {
        this.value = value;
        this.desc = desc;
        this.score = score;
    }

    public static HorizonEnum forValue(Integer value) {
        for (HorizonEnum horizonEnum : HorizonEnum.values()) {
            if (Objects.equals(horizonEnum.getValue(), value)) {
                return horizonEnum;
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
