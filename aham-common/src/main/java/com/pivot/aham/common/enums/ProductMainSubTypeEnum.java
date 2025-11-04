package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 产品main或sub类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ProductMainSubTypeEnum implements IEnum {
    MAIN(1, "Main ETF"),
    SUB(2, "Sub ETF");

    private int value;
    private String desc;

    ProductMainSubTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ProductMainSubTypeEnum forValue(Integer value) {
        for (ProductMainSubTypeEnum investTypeEnum : ProductMainSubTypeEnum.values()) {
            if (Objects.equals(investTypeEnum.getValue(), value)) {
                return investTypeEnum;
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
