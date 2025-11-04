package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 产品资产状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum ProductAssetStatusEnum implements IEnum {
    BUY_ING(1, "买入中"),
    HOLD_ING(2, "持有中"),
    SELL_ING(3, "卖出中"),
    CONFIRM_SELL(4, "卖出确认"),
    CONVERT_ING(5, "转换在途"),
    UN_VALID(6, "无效"),
    ;

    ProductAssetStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;
    private String desc;

    @Override
    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static ProductAssetStatusEnum forValue(Integer value) {
        for (ProductAssetStatusEnum productAssetStatusEnum : ProductAssetStatusEnum.values()) {
            if (Objects.equals(productAssetStatusEnum.value, value)) {
                return productAssetStatusEnum;
            }
        }
        return null;
    }
}
