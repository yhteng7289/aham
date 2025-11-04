package com.pivot.aham.common.enums.in;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * Created by luyang.li on 18/11/30.
 */
public enum RoleTypeEnum implements IEnum {
    LEVEL_1(1, "Normal Role"),
    LEVEL_2(2, "System Role"),
    LEVEL_3(3, "Admin"),
    ;

    private Integer value;
    private String desc;

    RoleTypeEnum(Integer value, String desc ) {
        this.value = value;
        this.desc = desc;
    }

    public static RoleTypeEnum forValue(Integer value) {
        for (RoleTypeEnum ageEnum : RoleTypeEnum.values()) {
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

}
