package com.pivot.aham.common.enums.app;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author YYYz
 */

public enum CostCountriesTypeEnum implements IEnum {
    Singapore(1,"Singapore"),
    Australia(2,"Australia"),
    UnitedKingdom(3,"United Kingdom"),
    UnitedStates(4,"United States"),
    ;

    CostCountriesTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static CostCountriesTypeEnum forValue(Integer value) {
        for (CostCountriesTypeEnum costCountriesTypeEnum : CostCountriesTypeEnum.values()) {
            if (Objects.equals(costCountriesTypeEnum.getValue(), value)) {
                return costCountriesTypeEnum;
            }
        }
        return null;
    }

    private Integer value;
    private String desc;

    @Override
    public Serializable getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
