package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum RebalanceEnum implements IEnum {
    REBALANCE(1, "重新执行了调仓"),
    UN_REBALANCE(0, "未执行调仓");

    private int value;
    private String desc;

    RebalanceEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static RebalanceEnum forValue(Integer value) {
        for (RebalanceEnum rebalanceEnum : RebalanceEnum.values()) {
            if (Objects.equals(rebalanceEnum.getValue(), value)) {
                return rebalanceEnum;
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
