package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum UobExecutionOrderStatusEnum implements IEnum {
    WAIT_EXECUTE(0, "待执行"),
    WAIT_CONFIRM(1, "待确认"),
    WAIT_REVERSE(2, "待反转"),
    FINISH(3, "完成"),
    ;

    private int value;
    private String desc;

    UobExecutionOrderStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UobExecutionOrderStatusEnum forValue(Integer value) {
        for (UobExecutionOrderStatusEnum investTypeEnum : UobExecutionOrderStatusEnum.values()) {
            if (Objects.equals(investTypeEnum.getValue(), value)) {
                return investTypeEnum;
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
