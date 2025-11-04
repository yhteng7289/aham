package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ExecuteOrderTadeTypeEnum implements IEnum {
    WITHDRAWAL(1, "卖单"),
    BUY(2, "买单"),
    BALANCE(3, "调仓")
    ;
    private Integer value;
    private String desc;

    ExecuteOrderTadeTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static VAOrderTradeStatusEnum forValue(Integer value) {
        for (VAOrderTradeStatusEnum accountOrderEnum : VAOrderTradeStatusEnum.values()) {
            if (Objects.equals(accountOrderEnum.getValue(), value)) {
                return accountOrderEnum;
            }
        }
        return null;
    }


    @Override
    public Integer getValue() {
        return null;
    }

    public String getDesc() {
        return null;
    }
}
