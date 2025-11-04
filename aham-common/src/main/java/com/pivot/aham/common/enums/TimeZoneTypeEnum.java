package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 购汇类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum TimeZoneTypeEnum implements IEnum {
    america(1,"America/New_York"),
    defaultTimezone(2, "Asia/Shanghai"),
    ;

    private Integer value;
    private String desc;

    TimeZoneTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TimeZoneTypeEnum forValue(Integer value) {
        for (TimeZoneTypeEnum ageEnum : TimeZoneTypeEnum.values()) {
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
