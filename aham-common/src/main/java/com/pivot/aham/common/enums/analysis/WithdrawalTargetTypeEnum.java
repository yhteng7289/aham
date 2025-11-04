package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 提现目标类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum WithdrawalTargetTypeEnum implements IEnum {
    DEFAULT(-1, "default"),
    SquirrelCashAccount(1, "SquirrelCashAccount"),
    BankAccount(2, "BankAccount");

    private Integer value;
    private String desc;

    WithdrawalTargetTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static WithdrawalTargetTypeEnum forValue(Integer value) {
        for (WithdrawalTargetTypeEnum targetTypeEnum : WithdrawalTargetTypeEnum.values()) {
            if (Objects.equals(targetTypeEnum.getValue(), value)) {
                return targetTypeEnum;
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
