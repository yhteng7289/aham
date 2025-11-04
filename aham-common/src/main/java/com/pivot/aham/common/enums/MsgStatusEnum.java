package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum MsgStatusEnum implements IEnum {
    CREATE(1, "创建"),
    SENDED(2, "已发送"),
    CONSUMERED(3, "已消费"),
    CONSUMERFAIL(4,"消费失败"),
    EXCHANGE_FAILED(5, "exchange到queue失败");

    private int value;
    private String desc;

    MsgStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static MsgStatusEnum forValue(Integer value) {
        for (MsgStatusEnum investTypeEnum : MsgStatusEnum.values()) {
            if (Objects.equals(investTypeEnum.getValue(), value)) {
                return investTypeEnum;
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
