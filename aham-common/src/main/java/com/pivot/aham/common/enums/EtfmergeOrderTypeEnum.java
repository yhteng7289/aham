package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-06-27 16:34
 **/
public enum EtfmergeOrderTypeEnum implements IEnum {
    BUY(1, "买"),
    SELL(2, "卖"),
    DO_NOTHING(3, "do nothing"),
    ;

    private int value;
    private String desc;

    EtfmergeOrderTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static EtfmergeOrderTypeEnum forValue(Integer value) {
        for (EtfmergeOrderTypeEnum typeEnum : EtfmergeOrderTypeEnum.values()) {
            if (Objects.equals(typeEnum.getValue(), value)) {
                return typeEnum;
            }
        }
        return null;
    }


    @Override
    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}

