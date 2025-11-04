package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum PftHoldingStatusEnum implements IEnum {
    HOLDING(1, "HOLDING"),
    COMPLETE(2, "COMPLETE");

    private int value;
    private String desc;

    PftHoldingStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static PftHoldingStatusEnum forValue(Integer value) {
        for (PftHoldingStatusEnum investTypeEnum : PftHoldingStatusEnum.values()) {
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
