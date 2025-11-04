package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 *
 * @author WooiTatt
 */
public enum  UserBatchNavEnum  implements IEnum {
    ACTIVE(1, "ACTIVE"),
    DEACTIVE(2,"DEACTIVE")
    ;


    private Integer value;
    private String desc;

    UserBatchNavEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static UserBatchNavEnum forValue(Integer value) {
        for (UserBatchNavEnum accountOrderEnum : UserBatchNavEnum.values()) {
            if (Objects.equals(accountOrderEnum.getValue(), value)) {
                return accountOrderEnum;
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
