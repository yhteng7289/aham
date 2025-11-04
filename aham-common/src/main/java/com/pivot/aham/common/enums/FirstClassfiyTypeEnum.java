package com.pivot.aham.common.enums;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 产品一级分类
 */
public enum FirstClassfiyTypeEnum implements IEnum {
    FIXED_INCOME(1, "FixedIncome"),
    ALTERNATIVE(2, "Alternative"),
    DEVELOPED_EQUITY(3, "DevelopedEquity"),
    EMERGING_EQUITY(4, "EmergingEquity"),
    CASH2(5, "Cash2"),
    CURRENCY(6, "Currency")
    ;

    private int value;
    private String desc;

    FirstClassfiyTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static FirstClassfiyTypeEnum forValue(Integer value) {
        for (FirstClassfiyTypeEnum firstClassfiyType : FirstClassfiyTypeEnum.values()) {
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

    /**
     * 该方法是为了进行模型Excel中的表格头的匹配,其他业务逻辑中慎用
     *
     * @param productCode
     * @return
     */
    public static FirstClassfiyTypeEnum forDesc(String productCode) {
        for (FirstClassfiyTypeEnum firstClassfiyTypeEnum : FirstClassfiyTypeEnum.values()) {
            if (firstClassfiyTypeEnum.desc.equals(productCode)) {
                return firstClassfiyTypeEnum;
            }
        }
        return null;
    }
}
