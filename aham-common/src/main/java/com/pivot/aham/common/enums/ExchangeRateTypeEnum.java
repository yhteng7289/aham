package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ExchangeRateTypeEnum implements IEnum {
    DEFAULT(0, "DEFAULT"),
    SAXO_FXRT1(1, "SAXO FXRT1"),
    SAXO_FXRT2(2, "SAXO FXRT2"),
    UOB_SGD_TO_USD(3, "UOB SGD TO USD"),
    UOB_USD_TO_SGD(4, "UOB USD TO SGD")
    ;

    private int value;
    private String desc;

    ExchangeRateTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ExchangeRateTypeEnum forValue(Integer value) {
        for (ExchangeRateTypeEnum exchangeRateTypeEnum : ExchangeRateTypeEnum.values()) {
            if (Objects.equals(exchangeRateTypeEnum.getValue(), value)) {
                return exchangeRateTypeEnum;
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
