package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum SaxoOrderFeeStatusEnum implements IEnum {
    WAIT_NOTIFY(0, "待通知"),
    FINISH(1, "完成"),
    ;

    private int value;
    private String desc;

    SaxoOrderFeeStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static SaxoOrderFeeStatusEnum forValue(Integer value) {
        for (SaxoOrderFeeStatusEnum investTypeEnum : SaxoOrderFeeStatusEnum.values()) {
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
