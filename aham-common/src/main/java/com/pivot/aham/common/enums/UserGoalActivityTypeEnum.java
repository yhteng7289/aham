package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum UserGoalActivityTypeEnum implements IEnum {
    RECHARGE(1, "充值"),
    WITHDRAWAL(2, "提现");

    private int value;
    private String desc;

    UserGoalActivityTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserGoalActivityTypeEnum forValue(Integer value) {
        for (UserGoalActivityTypeEnum investTypeEnum : UserGoalActivityTypeEnum.values()) {
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
