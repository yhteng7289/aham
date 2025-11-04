package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * 银行划款状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum BalStatusEnum implements IEnum {
    HANDLING(0,"新建"),
    SELLING(1,"卖执行中"),
    BUYING(2,"买执行中"),
    SUCCESS(3,"执行完成"),
    ;

    private Integer value;
    private String desc;

    BalStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static BalStatusEnum forValue(Integer value) {
        for (BalStatusEnum balTradeTypeEnum : BalStatusEnum.values()) {
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
