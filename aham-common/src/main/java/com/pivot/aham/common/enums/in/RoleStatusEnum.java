package com.pivot.aham.common.enums.in;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum RoleStatusEnum implements IEnum {
    NORMAL(0, "正常"),
    LOCK(1, "锁定")
    ;

    private Integer value;
    private String desc;

    RoleStatusEnum(Integer value, String desc ) {
        this.value = value;
        this.desc = desc;
    }

    public static RoleStatusEnum forValue(Integer value) {
        for (RoleStatusEnum ageEnum : RoleStatusEnum.values()) {
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
