package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * SAXO账户订单交易类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum SaxoOrderTradeTypeEnum implements IEnum {
    COME_INTO(1, "收入"),
    COME_OUT(2, "支出")
//    //SAXO账户 与外部账户的转账
//    TRANSFER_INTO(3, "转账收入"),
//    //SAXO账户 与外部账户的转账
//    TRANSFER_OUT(4, "转账支出"),
//    //SAXO账户 从SGD转USD,或者从USD转SGD 对于后者就是收入
//    EXCHANGE_INTO(5, "购汇收入"),
//    //SAXO账户 从SGD转USD,或者从USD转SGD 对于前者就是支出
//    EXCHANGE_OUT(6, "购汇支出"),
//    REFUND_OUT(7, "退款支出")
    ;

    private Integer value;
    private String desc;

    SaxoOrderTradeTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static SaxoOrderTradeTypeEnum forValue(Integer value) {
        for (SaxoOrderTradeTypeEnum accountOrderEnum : SaxoOrderTradeTypeEnum.values()) {
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
