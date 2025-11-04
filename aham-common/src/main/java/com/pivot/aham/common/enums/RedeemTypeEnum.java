package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * 货币类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum RedeemTypeEnum implements IEnum {
    ALLRedeem(2,"全部赎回"),
    NOTALLRedeem(1, "非全部赎回"),
    ;

    private Integer value;
    private String desc;

    RedeemTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static RedeemTypeEnum forValue(Integer value) {
        for (RedeemTypeEnum ageEnum : RedeemTypeEnum.values()) {
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
