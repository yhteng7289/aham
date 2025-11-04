package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum UobTransferOrderTypeEnum implements IEnum {
    TRANSFER_TO_BANK(1, "转账到银行卡"),
    TRANSFER_TO_SAXO(2, "转账到SAXO"),
    ;

    private int value;
    private String desc;

    UobTransferOrderTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UobTransferOrderTypeEnum forValue(Integer value) {
        for (UobTransferOrderTypeEnum investTypeEnum : UobTransferOrderTypeEnum.values()) {
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
