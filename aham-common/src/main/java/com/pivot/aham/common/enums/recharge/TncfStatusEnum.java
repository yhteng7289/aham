package com.pivot.aham.common.enums.recharge;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum TncfStatusEnum implements IEnum {
    PROCESSING(0, "处理中"),
    TNCF(1, "TNCF"),
    ASSETSELLCOMPLETE(4, "ASSETSELLCOMPLETE"), // Added WooiTatt *Purpose: Make sure all ETF is SELL, Then only proceed to NAV
    SUCCESS(2, "成功"),
    FAIL(3, "失败");

    private int value;
    private String desc;

    TncfStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TncfStatusEnum forValue(Integer value) {
        for (TncfStatusEnum tncfStatusEnum : TncfStatusEnum.values()) {
            if (Objects.equals(tncfStatusEnum.getValue(), value)) {
                return tncfStatusEnum;
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
