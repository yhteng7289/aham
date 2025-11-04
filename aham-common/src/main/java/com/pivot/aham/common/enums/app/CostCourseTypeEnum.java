package com.pivot.aham.common.enums.app;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author YYYz
 */
public enum CostCourseTypeEnum implements IEnum {
    ARTS(1,"Arts"),
    AL(2,"Architecture / Law"),
    BA(3,"Business / Accountancy"),
    ESC(4,"Engineering / Science / Computing"),
    MED(5,"Medicine (5-year course)"),
    ;

    CostCourseTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;
    private String desc;

    public static CostCourseTypeEnum forValue(Integer value) {
        for (CostCourseTypeEnum courseTypeEnum : CostCourseTypeEnum.values()) {
            if (Objects.equals(courseTypeEnum.getValue(), value)) {
                return courseTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Serializable getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
