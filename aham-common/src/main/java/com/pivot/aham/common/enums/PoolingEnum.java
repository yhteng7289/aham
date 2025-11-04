package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 投资年限
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum PoolingEnum implements IEnum {
    P1(1, "第一个投资年限:1-3年", "POOL1"),
    P2(2, "第二个投资年限:3-5年", "POOL2"),
    P3(3, "第三个投资年限:>5年", "POOL3");

    private Integer value;
    private String desc;
    private String name;

    PoolingEnum(Integer value, String desc, String name) {
        this.value = value;
        this.desc = desc;
        this.name = name;
    }

    public static PoolingEnum forValue(Integer value) {
        for (PoolingEnum poolingEnum : PoolingEnum.values()) {
            if (Objects.equals(poolingEnum.getValue(), value)) {
                return poolingEnum;
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


}
