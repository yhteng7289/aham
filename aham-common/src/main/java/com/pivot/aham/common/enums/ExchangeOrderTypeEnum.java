package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 购汇类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ExchangeOrderTypeEnum implements IEnum {
    WITHDRAW(1,"提现"),
    RECHARGE(2, "入金"),
    ;

    private Integer value;
    private String desc;

    ExchangeOrderTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ExchangeOrderTypeEnum forValue(Integer value) {
        for (ExchangeOrderTypeEnum ageEnum : ExchangeOrderTypeEnum.values()) {
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
