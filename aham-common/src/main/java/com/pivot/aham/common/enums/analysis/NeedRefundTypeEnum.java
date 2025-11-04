package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum NeedRefundTypeEnum implements IEnum {
    UN_REFUND(0, "非退款"),
    REFUND(1, "退款")
    ;

    private int value;
    private String desc;

    NeedRefundTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static NeedRefundTypeEnum forValue(Integer value) {
        for (NeedRefundTypeEnum bankOrderRefundTypeEnum : NeedRefundTypeEnum.values()) {
            if (Objects.equals(bankOrderRefundTypeEnum.getValue(), value)) {
                return bankOrderRefundTypeEnum;
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
