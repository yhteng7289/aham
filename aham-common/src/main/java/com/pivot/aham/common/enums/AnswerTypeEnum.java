package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum AnswerTypeEnum implements IEnum {

    LOSE(0, "LOSE"),
    WIN(1, "WIN"),
    SKIP(2, "SKIP");

    private Integer value;
    private String desc;


    AnswerTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;

    }

    public static AnswerTypeEnum forValue(Integer value) {
        for (AnswerTypeEnum answerTypeEnum : AnswerTypeEnum.values()) {
            if (Objects.equals(answerTypeEnum.getValue(), value)) {
                return answerTypeEnum;
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
