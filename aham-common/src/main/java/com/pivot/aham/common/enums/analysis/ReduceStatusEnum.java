package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月01日
 */
public enum ReduceStatusEnum implements IEnum {
    NOT_REDUCE(0,"未扣减"),
    HAS_REDUCE(1,"已扣减");

    private int value;
    private String desc;

    ReduceStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ReduceStatusEnum forValue(Integer value) {
        for (ReduceStatusEnum initDayEnum : ReduceStatusEnum.values()) {
            if (Objects.equals(initDayEnum.getValue(), value)) {
                return initDayEnum;
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
