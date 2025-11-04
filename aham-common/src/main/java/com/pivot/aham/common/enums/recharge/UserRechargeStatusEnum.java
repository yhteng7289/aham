package com.pivot.aham.common.enums.recharge;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 *
 * @author WooiTatt
 * @since 2020
 */

public enum UserRechargeStatusEnum implements IEnum {
    PROGRESS(0, "PROGRESS"),
    PROGRESSUOBTOSAXO(1,"PROGRESSUOBTOSAXO"),
    INTERTRANSTOUSD(2,"INTERTRANSTOUSD"),
    SUCCESS(3, "SUCCESS"),
    FAIL(4, "FAILED");

    private int value;
    private String desc;

    UserRechargeStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserRechargeStatusEnum forValue(Integer value) {
        for (UserRechargeStatusEnum uobRechargeStatusEnum : UserRechargeStatusEnum.values()) {
            if (Objects.equals(uobRechargeStatusEnum.getValue(), value)) {
                return uobRechargeStatusEnum;
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
