package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 *
 * 虚拟账户订单交易状态
 * @author addison
 * @since 2018年12月13日
 */
public enum VAOrderTradeStatusEnum implements IEnum {
    HANDLING(1, "处理中"),
    SUCCESS(2, "成功"),
    FAIL(3, "失败")
    ;

    private Integer value;
    private String desc;

    VAOrderTradeStatusEnum(int value, String desc) {
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

    public Integer getValue() {
        return this.value;
    }

    public String getDesc() {
        return desc;
    }
}
