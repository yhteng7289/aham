package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 账户类型
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum AhamReconResultEnum implements IEnum {
	DEFAULT(-1, "DEFAULT"),
	BALANCED(1, "BALANCED"),
    IMBALANCED(2, "IMBALANCED")
    ;

	private int value;
    private String desc;

    AhamReconResultEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static AhamReconResultEnum forValue(Integer value) {
        for (AhamReconResultEnum investTypeEnum : AhamReconResultEnum.values()) {
            if (Objects.equals(investTypeEnum.getValue(), value)) {
                return investTypeEnum;
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
