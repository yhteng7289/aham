package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 产品交易状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ProductTradeStatusEnum implements IEnum {
    TRADE(0, "交易"),
    STOP(1, "停止交易");

    private int value;
    private String desc;

    ProductTradeStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ProductTradeStatusEnum forValue(Integer value) {
        for (ProductTradeStatusEnum investTypeEnum : ProductTradeStatusEnum.values()) {
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
