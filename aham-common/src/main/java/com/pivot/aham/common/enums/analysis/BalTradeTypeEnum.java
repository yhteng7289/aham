package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * 调仓交易类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum BalTradeTypeEnum implements IEnum {
    BUY(0,"买"),
    SELL(1,"卖"),
    ;

    private Integer value;
    private String desc;

    BalTradeTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static BalTradeTypeEnum forValue(Integer value) {
        for (BalTradeTypeEnum balTradeTypeEnum : BalTradeTypeEnum.values()) {
            if (Objects.equals(balTradeTypeEnum.getValue(), value)) {
                return balTradeTypeEnum;
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
