package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 产品一级分类
 */
public enum ExcelTitleTypeEnum implements IEnum {
    ETF(1, "etf"),
    SCORE(2, "Score"),
    RISK(3, "Risk"),
    AGE(4, "Age"),
    FIRST_CLASSFIY(5, "first_classfiy"),
    DATE(6, "Date"),
    CURRENCY(7, "Currency")
    ;

    private int value;
    private String desc;

    ExcelTitleTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ExcelTitleTypeEnum forValue(Integer value) {
        for (ExcelTitleTypeEnum firstClassfiyType : ExcelTitleTypeEnum.values()) {
            if (Objects.equals(firstClassfiyType.getValue(), value)) {
                return firstClassfiyType;
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

    public static ExcelTitleTypeEnum forDesc(String excelTitleValue) {
        for (ExcelTitleTypeEnum excelTitleTypeEnum : ExcelTitleTypeEnum.values()) {
            if (excelTitleTypeEnum.getDesc().equals(excelTitleValue)) {
                return excelTitleTypeEnum;
            }
        }
        return null;
    }
}
