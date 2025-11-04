package com.pivot.aham.common.enums.in;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum TransStatusEnum implements IEnum {
    COMPLETED(2, "完成"),
    HANDLING(1, "处理中"),
    PROCESSING(0, "处理中1"),
    FAILED(3, "失败"),
    ;

    private Integer value;
    private String desc;

    TransStatusEnum(Integer value, String desc ) {
        this.value = value;
        this.desc = desc;
    }

    public static TransStatusEnum forValue(Integer value) {
        for (TransStatusEnum ageEnum : TransStatusEnum.values()) {
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
