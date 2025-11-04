package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月06日
 */
public enum PftAssetOperateTypeEnum implements IEnum {
    NEEDETFSHARES(0, "出etf份额"),
    NEEDCASH(1, "出cash");
    ;


    private Integer value;
    private String desc;

    PftAssetOperateTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static PftAssetOperateTypeEnum forValue(Integer value) {
        for (PftAssetOperateTypeEnum accountOrderEnum : PftAssetOperateTypeEnum.values()) {
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
