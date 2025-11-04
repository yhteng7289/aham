package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum EtfOrderStatusEnum implements IEnum {
    WAIT_MERGE(0, "待合并"),
    WAIT_TRADE(1, "待交易"),
    WAIT_NOTIFY(2, "待通知"),
    FINISH(3, "完成"),
    ;

    private int value;
    private String desc;

    EtfOrderStatusEnum(int value, String desc) {
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
