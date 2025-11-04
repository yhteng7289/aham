package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum NameAliasManageStatusEnum implements IEnum {

    APPROVE(1, "批准"),
    REJECT(0, "拒绝");

    private final int value;
    private final String desc;

    NameAliasManageStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static NameAliasManageStatusEnum forValue(Integer value) {
        for (NameAliasManageStatusEnum nameAliasManageStatusEnum : NameAliasManageStatusEnum.values()) {
            if (Objects.equals(nameAliasManageStatusEnum.getValue(), value)) {
                return nameAliasManageStatusEnum;
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
