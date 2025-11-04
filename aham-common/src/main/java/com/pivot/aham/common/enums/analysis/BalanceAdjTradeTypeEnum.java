package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月06日
 */
public enum BalanceAdjTradeTypeEnum implements IEnum {
    BUY(0,"买"),
    SELL(1,"卖"),
    ;


    private Integer value;
    private String desc;

    BalanceAdjTradeTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static BalanceAdjTradeTypeEnum forValue(Integer value) {
        for (BalanceAdjTradeTypeEnum balanceStatusEnum : BalanceAdjTradeTypeEnum.values()) {
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
