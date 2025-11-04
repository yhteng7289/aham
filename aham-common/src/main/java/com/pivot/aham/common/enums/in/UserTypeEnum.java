package com.pivot.aham.common.enums.in;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * Created by luyang.li on 18/11/30.
 */
public enum UserTypeEnum implements IEnum {
    LEVEL_1(1, "Normal User"),
    LEVEL_2(2, "System User"),
    LEVEL_3(3, "Admin"),
    ;

    private Integer value;
    private String desc;

    UserTypeEnum(Integer value, String desc ) {
        this.value = value;
        this.desc = desc;
    }

    public static UserTypeEnum forValue(Integer value) {
        for (UserTypeEnum ageEnum : UserTypeEnum.values()) {
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
