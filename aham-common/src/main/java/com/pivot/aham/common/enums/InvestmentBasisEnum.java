package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.math.BigDecimal;
import java.util.Objects;

public enum InvestmentBasisEnum implements IEnum {

    LEVEL1(1, "LEVEL1", new BigDecimal(10)),
    LEVEL2(2, "LEVEL2", new BigDecimal(4)),
    LEVEL3(3, "LEVEL3", new BigDecimal(8)),
    LEVEL4(4, "LEVEL4", new BigDecimal(2)),
    LEVEL5(5, "LEVEL5", new BigDecimal(6));

    private Integer value;
    private String desc;
    private BigDecimal score;

    InvestmentBasisEnum(Integer value, String desc, BigDecimal score) {
        this.value = value;
        this.desc = desc;
        this.score = score;
    }

    public static InvestmentBasisEnum forValue(Integer value) {
        for (InvestmentBasisEnum investmentBasisEnum : InvestmentBasisEnum.values()) {
            if (Objects.equals(investmentBasisEnum.getValue(), value)) {
                return investmentBasisEnum;
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
