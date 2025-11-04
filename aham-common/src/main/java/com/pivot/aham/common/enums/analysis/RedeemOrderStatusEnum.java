package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum RedeemOrderStatusEnum implements IEnum {
    DEEFAULT(-1, "默认值"),
    PROCESSING(0, "待处理"),
    HANDLING(1, "etf已下单"),
    SUCCESS(2, "成功"),
    FAIL(3, "失败")
    ;

    private int value;
    private String desc;

    RedeemOrderStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static RedeemOrderStatusEnum forValue(Integer value) {
        for (RedeemOrderStatusEnum rechargeOrderStatusEnum : RedeemOrderStatusEnum.values()) {
            if (Objects.equals(rechargeOrderStatusEnum.getValue(), value)) {
                return rechargeOrderStatusEnum;
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
