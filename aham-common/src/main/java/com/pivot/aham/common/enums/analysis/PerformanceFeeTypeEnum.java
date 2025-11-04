package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 *
 * @author WooiTatt
 */
public enum  PerformanceFeeTypeEnum  implements IEnum {
    WITHDRAW(1, "WITHDRAW"),
    ANNUAL(2,"ANNUAL")
    ;


    private Integer value;
    private String desc;

    PerformanceFeeTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static PerformanceFeeTypeEnum forValue(Integer value) {
        for (PerformanceFeeTypeEnum accountOrderEnum : PerformanceFeeTypeEnum.values()) {
            if (Objects.equals(accountOrderEnum.getValue(), value)) {
                return accountOrderEnum;
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
