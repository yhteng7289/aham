package com.pivot.aham.common.enums.recharge;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum UobRechargeStatusEnum implements IEnum {
    PROCESSING(0, "处理中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

    private int value;
    private String desc;

    UobRechargeStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UobRechargeStatusEnum forValue(Integer value) {
        for (UobRechargeStatusEnum uobRechargeStatusEnum : UobRechargeStatusEnum.values()) {
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
