package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 提现
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum WithdrawalSourceTypeEnum implements IEnum {
    DEFAULT(-1, "default"),
    FROMGOAL(1, "FORMGOAL"),
    FROMVIRTUALACCOUNT(2, "FORMVIRTUALACCOUNT"),
    ;

    private Integer value;
    private String desc;

    WithdrawalSourceTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static WithdrawalSourceTypeEnum forValue(Integer value) {
        for (WithdrawalSourceTypeEnum targetTypeEnum : WithdrawalSourceTypeEnum.values()) {
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
