package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * 货币类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum CurrencyEnum implements IEnum {
    DEFAULT(-1,"默认","default"),
    MYR(1, "Malaysia Ringgit", "MYR"),
    SGD(2, "Singapore Dollar", "SGD"),
    USD(3, "US Dollar", "USD")
    ;

    private Integer value;
    private String desc;
    private String code;

    CurrencyEnum(Integer value, String desc, String code) {
        this.value = value;
        this.desc = desc;
        this.code = code;
    }

    public static CurrencyEnum forValue(Integer value) {
        for (CurrencyEnum ageEnum : CurrencyEnum.values()) {
            if (Objects.equals(ageEnum.getValue(), value)) {
                return ageEnum;
            }
        }
        return null;
    }

    public static CurrencyEnum forCode(String code) {
        for (CurrencyEnum ageEnum : CurrencyEnum.values()) {
            if (Objects.equals(ageEnum.getCode(), code)) {
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

    public String getCode(){return code;}
}
