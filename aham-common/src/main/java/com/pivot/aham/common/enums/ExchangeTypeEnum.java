package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 购汇类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ExchangeTypeEnum implements IEnum {
    USD_SGD(1,"美元转新币"),
    SGD_USD(2, "新币转美元"),
    ;

    private Integer value;
    private String desc;

    ExchangeTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ExchangeTypeEnum forValue(Integer value) {
        for (ExchangeTypeEnum ageEnum : ExchangeTypeEnum.values()) {
            if (Objects.equals(ageEnum.getValue(), value)) {
                return ageEnum;
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
