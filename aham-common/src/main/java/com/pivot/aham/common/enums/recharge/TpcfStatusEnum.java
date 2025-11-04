package com.pivot.aham.common.enums.recharge;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum TpcfStatusEnum implements IEnum {
    PROCESSING(0, "处理中"),
    TPCF(1, "TPCF"),
    ASSETBUYCOMPLETE(4, "ASSETBUYCOMPLETE"), // Added WooiTatt *Purpose: Make sure all ETF is BUY, Then only proceed to NAV
    SUCCESS(2, "成功"),
    FAIL(3, "失败");

    private int value;
    private String desc;

    TpcfStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TpcfStatusEnum forValue(Integer value) {
        for (TpcfStatusEnum tpcfStatusEnum : TpcfStatusEnum.values()) {
            if (Objects.equals(tpcfStatusEnum.getValue(), value)) {
                return tpcfStatusEnum;
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
