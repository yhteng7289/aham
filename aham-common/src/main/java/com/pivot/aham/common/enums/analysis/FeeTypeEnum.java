package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum FeeTypeEnum implements IEnum {
    MGT_FEE(1, "mgtFee"),
    MGT_GST(2, "mgtGst"),
    PERFORMANCE_FEE(3, "performanceFee"),
    PERFORMANCE_GST(4, "performanceGst"),
    CUST_FEE(5, "custFee");

    private int value;
    private String desc;

    FeeTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static FeeTypeEnum forValue(Integer value) {
        for (FeeTypeEnum feeTypeEnum : FeeTypeEnum.values()) {
            if (Objects.equals(feeTypeEnum.getValue(), value)) {
                return feeTypeEnum;
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
