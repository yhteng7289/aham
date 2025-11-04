package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 费率来源
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum DividendHandelTypeEnum implements IEnum {
    DEFAULT(0, "未处理"),
    USED_NAV(1, "计算NAV使用"),
    USED_COMMONWEAL(2,"划归公益账户");

    private int value;
    private String desc;

    DividendHandelTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static DividendHandelTypeEnum forValue(Integer value) {
        for (DividendHandelTypeEnum initDayEnum : DividendHandelTypeEnum.values()) {
            if (Objects.equals(initDayEnum.getValue(), value)) {
                return initDayEnum;
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
