package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 模型状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ModelStatusEnum implements IEnum {
    Disable(0, "无效"),
    Effective(1, "有效");

    private int value;
    private String desc;

    ModelStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ModelStatusEnum forValue(Integer value) {
        for (ModelStatusEnum modelStatus : ModelStatusEnum.values()) {
            if (Objects.equals(modelStatus.getValue(), value)) {
                return modelStatus;
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
