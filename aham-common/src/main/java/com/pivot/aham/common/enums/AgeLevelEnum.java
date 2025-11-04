package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 年龄分级
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum AgeLevelEnum implements IEnum {
    LEVEL_0(0, "0", "Age0"),
    LEVEL_1(1, "0-25周岁", "Age1"),
    LEVEL_2(2, "26-35周岁", "Age2"),
    LEVEL_3(3, "36-45周岁", "Age3"),
    LEVEL_4(4, "46-55周岁", "Age4"),
    LEVEL_5(5, "Above55", "Age5")
    ;

    private Integer value;
    private String desc;
    private String name;

    AgeLevelEnum(Integer value, String desc, String name) {
        this.value = value;
        this.desc = desc;
        this.name = name;
    }

    public static AgeLevelEnum forValue(Integer value) {
        for (AgeLevelEnum ageEnum : AgeLevelEnum.values()) {
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

    public String getName() {
        return name;
    }
}
