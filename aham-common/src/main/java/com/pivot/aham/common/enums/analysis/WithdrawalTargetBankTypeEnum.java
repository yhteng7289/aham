package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 提现目标银行类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum WithdrawalTargetBankTypeEnum implements IEnum {
    DEFAULT(-1, "default"),
    LOCAL(1, "本地银行"),
    OVERSEA(2, "海外银行"),
    ;

    private Integer value;
    private String desc;

    WithdrawalTargetBankTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static WithdrawalTargetBankTypeEnum forValue(Integer value) {
        for (WithdrawalTargetBankTypeEnum targetBankTypeEnum : WithdrawalTargetBankTypeEnum.values()) {
            if (Objects.equals(targetBankTypeEnum.getValue(), value)) {
                return targetBankTypeEnum;
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
