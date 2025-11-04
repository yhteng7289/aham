package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月06日
 */
public enum PftAssetSourceEnum implements IEnum {
    NORMALSELL(1, "普通卖"),
    NORMALBUY(2,"普通买"),
    ONLYSELL(3,"单独卖"),
    DIVIDEND(4,"分红"),
    ;


    private Integer value;
    private String desc;

    PftAssetSourceEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static PftAssetSourceEnum forValue(Integer value) {
        for (PftAssetSourceEnum accountOrderEnum : PftAssetSourceEnum.values()) {
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
