package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum EtfMergeOrderStatusEnum implements IEnum {
    WAIT_TRADE(0, "待交易"),
    WAIT_CONFIRM(1, "待确认"),
    WAIT_DEMERGE(2, "待拆分"),
    FINISH(3, "完成"),
    ;

    private int value;
    private String desc;

    EtfMergeOrderStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static EtfMergeOrderStatusEnum forValue(Integer value) {
        for (EtfMergeOrderStatusEnum investTypeEnum : EtfMergeOrderStatusEnum.values()) {
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
