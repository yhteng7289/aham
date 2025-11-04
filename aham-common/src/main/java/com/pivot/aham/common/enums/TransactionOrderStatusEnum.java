package com.pivot.aham.common.enums;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum TransactionOrderStatusEnum implements IEnum {
    PROCESSING(1, "处理中"),
    SUCCESS(2, "成功"),    
    FAIL(3, "失败");

    private int value;
    private String desc;

    TransactionOrderStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TransactionOrderStatusEnum forValue(Integer value) {
        for (TransactionOrderStatusEnum rechargeOrderStatusEnum : TransactionOrderStatusEnum.values()) {
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
