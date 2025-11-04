package com.pivot.aham.common.enums.in;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum MenuTypeEnum implements IEnum {
    BUTTON(0, "按钮"),
    MENU(1, "菜单"),
    INTERFACE(2, "接口")
    ;

    private Integer value;
    private String desc;

    MenuTypeEnum(Integer value, String desc ) {
        this.value = value;
        this.desc = desc;
    }

    public static MenuTypeEnum forValue(Integer value) {
        for (MenuTypeEnum ageEnum : MenuTypeEnum.values()) {
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
