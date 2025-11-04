package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum UobOrderStatusEnum implements IEnum {
    FAILED(-1, "未完成"),
    WAIT_CREATE_ORDER(0, "待生成执行单"),
    WAIT_EXECUTE(1, "待执行"),
    WAIT_CONFIRM(2, "待确认"),
    WAIT_NOTIFY(3, "待通知"),
    FINISH(4, "完成"),
    ;

    private int value;
    private String desc;

    UobOrderStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UobOrderStatusEnum forValue(Integer value) {
        for (UobOrderStatusEnum investTypeEnum : UobOrderStatusEnum.values()) {
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
