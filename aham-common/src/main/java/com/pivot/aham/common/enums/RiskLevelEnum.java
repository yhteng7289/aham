package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.math.BigDecimal;
import java.util.Objects;


/**
 * Created by luyang.li on 18/11/30.
 */
public enum RiskLevelEnum implements IEnum {
    DEFAULT(0, "DEFAULT", "Risk0", "DEFAULT", new BigDecimal("0000"), "Default"),
    CONSERVATIVE(1, "CONSERVATIVE", "Risk1", "CONSERVATIVE", new BigDecimal("0.0385"), "You are a conservative investor preferring to minimise risk and preserve capital.\n\nWe recommend highly diversified investing to achieve long-term asset appreciation. Most of your investments should be in low-volatility asset classes to mitigate potential market declines.\n\n Your tolerance for market volatility is around 5%."),
    BALANCED(2, "BALANCED", "Risk2", "BALANCED", new BigDecimal("0.0504"), "You are a balanced investor who values capital preservation but willing to accept some risk for long-term asset appreciation.\n\nYou are recommended to balance long-term risk and return through diversification.You can accept even exposure across low-risk and high-risk assets.\n\nYour tolerance for market volatility is around 8%."),
    GROWTH(3, "GROWTH", "Risk3", "GROWTH", new BigDecimal("0.0683"), "You are a growth-oriented investor willing to accept capital risk for higher long-term returns.\n\nYou accept high risk asset exposure while diversified across other asset classes to smooth out risk for long-term returns.\n\nYour tolerance for market volatility is 10% -15%."),
    AGGRESSIVE(4, "AGGRESSIVE", "Risk4", "AGGRESSIVE", new BigDecimal("0.0826"), "You are an aggressive investor willing to accept high risks to achieve higher returns.\n\nYou accept relatively high exposure to risky assets while diversified across other asset classes to smooth out risk for long-term returns.\n\nYour tolerance for market volatility is 15% -20%."),
    VERY_AGGRESSIVE(5, "VERY AGGRESSIVE", "Risk5", "VERY AGGRESSIVE", new BigDecimal("0.0909"), "You are a very aggressive investor who believes that high risks bring potentially higher returns.\n\nYou prefer most of your investments in high risk assets, but it is important to diversify by investing in assets with low correlations.\n\nYour tolerance for market volatility is 20% -25% or even higher.");

    private Integer value;
    private String desc;
    private String name;
    private String appName;
    private BigDecimal riskProfile;
    private String remark;

    RiskLevelEnum(Integer value, String desc, String name, String appName, BigDecimal riskProfile, String remark) {
        this.value = value;
        this.desc = desc;
        this.name = name;
        this.appName = appName;
        this.riskProfile = riskProfile;
        this.remark = remark;
    }

    public static RiskLevelEnum forValue(Integer value) {
        for (RiskLevelEnum riskEnum : RiskLevelEnum.values()) {
            if (Objects.equals(riskEnum.getValue(), value)) {
                return riskEnum;
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

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }

    public String getAppName() {
        return appName;
    }

    public BigDecimal getRiskProfile() {
        return riskProfile;
    }
}
