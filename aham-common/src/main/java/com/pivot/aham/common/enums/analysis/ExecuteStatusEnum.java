package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * 方案执行状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ExecuteStatusEnum implements IEnum {
    CREATE(0,"新建"),
    HANDLING(1,"执行中"),
    SUCCESS(2,"执行成功"),
    ;

    private Integer value;
    private String desc;

    ExecuteStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static ExecuteStatusEnum forValue(Integer value) {
        for (ExecuteStatusEnum balTradeTypeEnum : ExecuteStatusEnum.values()) {
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
