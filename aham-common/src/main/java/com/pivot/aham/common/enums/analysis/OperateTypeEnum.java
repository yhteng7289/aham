package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum OperateTypeEnum implements IEnum {
    RECHARGE(0, "入金"),
    WITHDRAW(1, "出金");

    private int value;
    private String desc;

    OperateTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static OperateTypeEnum forValue(Integer value) {
        for (OperateTypeEnum operateTypeEnum : OperateTypeEnum.values()) {
            if (Objects.equals(operateTypeEnum.getValue(), value)) {
                return operateTypeEnum;
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
