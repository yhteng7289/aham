package com.pivot.aham.common.enums.analysis;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 提现执行状态
 *
 * @author addison
 * @since 2018年12月11日
 */
public enum EtfExecutedStatusEnum implements IEnum {
    DEFAULT(-1, "default"),
    HANDLING(2, "处理中"),
    FAIL(3, "失败"),
    SUCCESS(4, "成功"),
    ;

    private Integer value;
    private String desc;

    EtfExecutedStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static EtfExecutedStatusEnum forValue(Integer value) {
        for (EtfExecutedStatusEnum redeemStatusEnum : EtfExecutedStatusEnum.values()) {
            if (Objects.equals(redeemStatusEnum.getValue(), value)) {
                return redeemStatusEnum;
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
