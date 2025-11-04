package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.math.BigDecimal;
import java.util.Objects;

public enum ToleranceEnum implements IEnum {

    LEVEL1(1, "LEVEL1", new BigDecimal(2)),
    LEVEL2(2, "LEVEL2", new BigDecimal(4)),
    LEVEL3(3, "LEVEL3", new BigDecimal(6)),
    LEVEL4(4, "LEVEL4", new BigDecimal(8)),
    LEVEL5(5, "LEVEL5", new BigDecimal(10)),
    LEVEL6(6, "LEVEL6", new BigDecimal(12));

    private Integer value;
    private String desc;
    private BigDecimal score;

    ToleranceEnum(Integer value, String desc, BigDecimal score) {
        this.value = value;
        this.desc = desc;
        this.score = score;
    }

    public static ToleranceEnum forValue(Integer value) {
        for (ToleranceEnum toleranceEnum : ToleranceEnum.values()) {
            if (Objects.equals(toleranceEnum.getValue(), value)) {
                return toleranceEnum;
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
