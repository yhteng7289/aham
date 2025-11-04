package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月06日
 */
public enum BalanceAdjExecuteStatusEnum implements IEnum {
    CREATE(0,"新建"),
    HANDLING(1,"执行中"),
    SUCCESS(2,"执行完成"),
    ;


    private Integer value;
    private String desc;

    BalanceAdjExecuteStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static BalanceAdjExecuteStatusEnum forValue(Integer value) {
        for (BalanceAdjExecuteStatusEnum balanceStatusEnum : BalanceAdjExecuteStatusEnum.values()) {
            if (Objects.equals(balanceStatusEnum.getValue(), value)) {
                return balanceStatusEnum;
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
