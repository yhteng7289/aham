package com.pivot.aham.common.enums.analysis;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum RechargeOrderStatusEnum implements IEnum {
    PROCESSING(0, "待上送"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败")
    ;

    private int value;
    private String desc;

    RechargeOrderStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static RechargeOrderStatusEnum forValue(Integer value) {
        for (RechargeOrderStatusEnum rechargeOrderStatusEnum : RechargeOrderStatusEnum.values()) {
            if (Objects.equals(rechargeOrderStatusEnum.getValue(), value)) {
                return rechargeOrderStatusEnum;
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
