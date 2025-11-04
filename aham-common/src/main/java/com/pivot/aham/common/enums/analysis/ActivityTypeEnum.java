package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ActivityTypeEnum implements IEnum {
    RECHARGE(1, "goal"),
    WITHDRAWAL(2, "aham");

    private int value;
    private String desc;

    ActivityTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ActivityTypeEnum forValue(Integer value) {
        for (ActivityTypeEnum investTypeEnum : ActivityTypeEnum.values()) {
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
