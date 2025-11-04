package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 *
 * SAXO 账户订单交易状态
 * @author addison
 * @since 2018年12月13日
 */
public enum SaxoOrderTradeStatusEnum implements IEnum {
    HANDLING(1, "处理中"),
    SUCCESS(2, "成功"),
    FAIL(3, "失败")
    ;

    private Integer value;
    private String desc;

    SaxoOrderTradeStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static SaxoOrderTradeStatusEnum forValue(Integer value) {
        for (SaxoOrderTradeStatusEnum accountOrderEnum : SaxoOrderTradeStatusEnum.values()) {
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
