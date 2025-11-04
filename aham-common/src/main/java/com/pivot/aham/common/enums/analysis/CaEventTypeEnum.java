package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年02月28日
 */
public enum CaEventTypeEnum  implements IEnum {
    CASH(10,"现金"),
    STOCK(70,"股票"),
    ;

    private Integer value;
    private String desc;

    CaEventTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static CaEventTypeEnum forValue(Integer value) {
        for (CaEventTypeEnum accountOrderEnum : CaEventTypeEnum.values()) {
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
