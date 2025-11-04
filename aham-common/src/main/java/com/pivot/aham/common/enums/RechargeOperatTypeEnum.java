package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * 货币类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum RechargeOperatTypeEnum implements IEnum {
    UN_INVEST(0, "未投资"),
    INVEST(1, "已投资"),
    ;

    private Integer value;
    private String desc;

    RechargeOperatTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static RechargeOperatTypeEnum forValue(Integer value) {
        for (RechargeOperatTypeEnum ageEnum : RechargeOperatTypeEnum.values()) {
            if (Objects.equals(ageEnum.getValue(), value)) {
                return ageEnum;
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
