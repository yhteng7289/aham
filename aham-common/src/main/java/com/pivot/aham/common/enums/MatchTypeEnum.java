package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 模型状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum MatchTypeEnum implements IEnum {
    MATCH(0, "匹配"),
    NAME_UNMATCH(1, "名字不匹配"),
    REFERENCECODE_UNMATCH(2, "referencecode不匹配");

    private int value;
    private String desc;

    MatchTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static MatchTypeEnum forValue(Integer value) {
        for (MatchTypeEnum modelStatus : MatchTypeEnum.values()) {
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
