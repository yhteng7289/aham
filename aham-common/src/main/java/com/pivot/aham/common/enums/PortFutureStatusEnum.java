package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 模型状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum PortFutureStatusEnum implements IEnum {
    Disable(0, "无效"),
    Effective(1, "有效");

    private int value;
    private String desc;

    PortFutureStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static PortFutureStatusEnum forValue(Integer value) {
        for (PortFutureStatusEnum portFutureStatusEnum : PortFutureStatusEnum.values()) {
            if (Objects.equals(portFutureStatusEnum.getValue(), value)) {
                return portFutureStatusEnum;
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
