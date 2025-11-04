package com.pivot.aham.common.enums.app;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author YYYz
 */
public enum GoalTypeEnum implements IEnum {
    BuildWealth(1, "BUILDWEALTH"),
    EducationSaving(2, "EDUCATIONSAVE"),
    RetirementSaving(3, "RETIREMENTSAVE"),
    GlobalSave(4, "GLOBALSAVE"),
    KidSave(5, "KIDSAVE");

    GoalTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static GoalTypeEnum forValue(Integer value) {
        for (GoalTypeEnum goalTypeEnum : GoalTypeEnum.values()) {
            if (Objects.equals(goalTypeEnum.getValue(), value)) {
                return goalTypeEnum;
            }
        }
        return null;
    }

    public static GoalTypeEnum forDesc(String desc) {
        for (GoalTypeEnum goalTypeEnum : GoalTypeEnum.values()) {
            if (Objects.equals(goalTypeEnum.getDesc(), desc)) {
                return goalTypeEnum;
            }
        }
        return null;
    }

    private Integer value;
    private String desc;

    @Override
    public Serializable getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
