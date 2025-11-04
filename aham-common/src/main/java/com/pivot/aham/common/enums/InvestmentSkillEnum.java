package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.math.BigDecimal;
import java.util.Objects;

public enum InvestmentSkillEnum implements IEnum {

    VERY_GOOD(1, "优", new BigDecimal(10)),
    GOOD(2, "良好", new BigDecimal(8)),
    AVERAGE(3, "平均", new BigDecimal(6)),
    FAIR(4, "较差", new BigDecimal(4)),
    POOR(5, "差", new BigDecimal(2));

    private Integer value;
    private String desc;
    private BigDecimal score;

    InvestmentSkillEnum(Integer value, String desc, BigDecimal score) {
        this.value = value;
        this.desc = desc;
        this.score = score;
    }

    public static InvestmentSkillEnum forValue(Integer value) {
        for (InvestmentSkillEnum investmentSkillEnum : InvestmentSkillEnum.values()) {
            if (Objects.equals(investmentSkillEnum.getValue(), value)) {
                return investmentSkillEnum;
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
