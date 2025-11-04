package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum TransferStatusEnum implements IEnum {
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

    private int value;
    private String desc;

    TransferStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TransferStatusEnum forValue(Integer value) {
        for (TransferStatusEnum transferStatusEnum : TransferStatusEnum.values()) {
            if (Objects.equals(transferStatusEnum.getValue(), value)) {
                return transferStatusEnum;
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
