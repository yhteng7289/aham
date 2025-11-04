package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * 虚拟账户动作来源
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum VAOrderActionTypeEnum implements IEnum {
    REDEEM_EXCHANGE(1, "提现购汇"),
    RECHARGE_EXCHANGE(2, "充值购汇"),
    REDEEM(3, "提现"),
    RECHARGE(4, "充值"),
    REFUND(5,"退款"),
    UOBTOSAXO(6,"UOBTOSAXO转账"),
    SAXOTOUOB(7,"SAXOTOUOB转账")
    ;

    private Integer value;
    private String desc;

    VAOrderActionTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static VAOrderActionTypeEnum forValue(Integer value) {
        for (VAOrderActionTypeEnum accountOrderEnum : VAOrderActionTypeEnum.values()) {
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
