package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum EtfOrderExtendStatusEnum implements IEnum {
    WAIT_CONFIRM(1, "待确认"),
    FINISH(2, "完成"),
    INVALID(3, "失效"),


    ;

    private int value;
    private String desc;

    EtfOrderExtendStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static EtfOrderStatusEnum forValue(Integer value) {
        for (EtfOrderStatusEnum investTypeEnum : EtfOrderStatusEnum.values()) {
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
