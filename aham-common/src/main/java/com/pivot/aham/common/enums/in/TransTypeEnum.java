package com.pivot.aham.common.enums.in;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum TransTypeEnum implements IEnum {
    INVESTMENT(0, "投资"),
    WITHDRAWAL(1, "提现"),
    DIVIDEND(2, "分红"),
    PERFORMANCEFEE(3, "附加费"),
    MGT(4, "管理费"),
    MGT_GST(5, "管理附加费"),
    CUSTODY(6, "CUSTODY")
    ;

    private Integer value;
    private String desc;

    TransTypeEnum(Integer value, String desc ) {
        this.value = value;
        this.desc = desc;
    }

    public static TransTypeEnum forValue(Integer value) {
        for (TransTypeEnum ageEnum : TransTypeEnum.values()) {
            if (Objects.equals(ageEnum.getValue(), value)) {
                return ageEnum;
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
