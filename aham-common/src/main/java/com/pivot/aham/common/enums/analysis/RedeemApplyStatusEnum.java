package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;


/**
 * 提现申请状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum RedeemApplyStatusEnum implements IEnum {
    HANDLING(0,"处理中"),
    SUCCESS(1,"处理成功"),
    FAIL(2, "处理失败"),
    ;

    private Integer value;
    private String desc;

    RedeemApplyStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static RedeemApplyStatusEnum forValue(Integer value) {
        for (RedeemApplyStatusEnum accountOrderEnum : RedeemApplyStatusEnum.values()) {
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
