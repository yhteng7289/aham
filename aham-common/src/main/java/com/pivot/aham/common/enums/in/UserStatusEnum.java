package com.pivot.aham.common.enums.in;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;
public enum UserStatusEnum implements IEnum {
    NORMAL(0, "正常"),
    LOCK(1, "锁定")
    ;

    private Integer value;
    private String desc;

    UserStatusEnum(Integer value, String desc ) {
        this.value = value;
        this.desc = desc;
    }

    public static UserStatusEnum forValue(Integer value) {
        for (UserStatusEnum ageEnum : UserStatusEnum.values()) {
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
