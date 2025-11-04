package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月06日
 */
public enum BalanceStatusEnum implements IEnum {
    HANDLING(0,"执行中"),
    SUCCESS(1,"执行成功"),
    ;


    private Integer value;
    private String desc;

    BalanceStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static BalanceStatusEnum forValue(Integer value) {
        for (BalanceStatusEnum balanceStatusEnum : BalanceStatusEnum.values()) {
            if (Objects.equals(balanceStatusEnum.getValue(), value)) {
                return balanceStatusEnum;
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
