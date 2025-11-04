package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum ErrorFeeTypeEnum implements IEnum {
    EXCHANGE_FEE(1, "exchangeFee"),
    EXTERNAL_CHARGES(2, "externalCharges"),
    PERFORMANCE_FEE(3, "performanceFee"),
    STAMP_DUTY(4, "stampDuty"),
    ACCOUNT(5,"account"),
    ARTIFICIALLEDGER(6,"artificialledger");

    private int value;
    private String desc;

    ErrorFeeTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ErrorFeeTypeEnum forValue(Integer value) {
        for (ErrorFeeTypeEnum errorFeeTypeEnum : ErrorFeeTypeEnum.values()) {
            if (Objects.equals(errorFeeTypeEnum.getValue(), value)) {
                return errorFeeTypeEnum;
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
