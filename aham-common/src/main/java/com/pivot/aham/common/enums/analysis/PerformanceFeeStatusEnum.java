package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 *
 * @author WooiTatt
 */
public enum  PerformanceFeeStatusEnum  implements IEnum {
    PROCESSING(1, "PROCESSING"),
    COMPLETED(2,"COMPLETED")
    ;


    private Integer value;
    private String desc;

    PerformanceFeeStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static PerformanceFeeStatusEnum forValue(Integer value) {
        for (PerformanceFeeStatusEnum accountOrderEnum : PerformanceFeeStatusEnum.values()) {
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
