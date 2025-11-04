package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * 银行划款状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum BankTransferStatusEnum implements IEnum {
    NOTEXCHANGE(0,"未购汇"),
    HASEXCHANGE(1,"已购汇"),
    SEND_SUCCESS(3, "发送成功"),
    SEND_FAIL(4, "发送失败"),
    UNKNOW(5,"未知"),
    CORRECT_ARRIVAL(6,"正确到账"),
    WRONG_ARRIVAL(7,"错误到账"),
    ;

    private Integer value;
    private String desc;

    BankTransferStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static BankTransferStatusEnum forValue(Integer value) {
        for (BankTransferStatusEnum accountOrderEnum : BankTransferStatusEnum.values()) {
            if (Objects.equals(accountOrderEnum.getValue(), value)) {
                return accountOrderEnum;
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
