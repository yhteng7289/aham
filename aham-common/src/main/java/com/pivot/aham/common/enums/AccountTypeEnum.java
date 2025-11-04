package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum AccountTypeEnum implements IEnum {
    POOLING(1, "公用户"),
    TAILOR(2, "定制户");

    private int value;
    private String desc;

    AccountTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static AccountTypeEnum forValue(Integer value) {
        for (AccountTypeEnum investTypeEnum : AccountTypeEnum.values()) {
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
