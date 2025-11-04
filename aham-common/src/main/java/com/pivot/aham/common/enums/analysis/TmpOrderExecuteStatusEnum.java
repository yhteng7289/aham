package com.pivot.aham.common.enums.analysis;


import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 临时订单状态
 *
 * @author addison
 * @since 2018年12月13日
 */
public enum TmpOrderExecuteStatusEnum implements IEnum {
    CREATE(0, "已创建"),
    HANDLING(1, "处理中"),
    SUCCESS(2, "确认成功"),
    FAIL(3, "确认失败"),
    SEND_FAIL(4, "发单失败")
    ;

    private Integer value;
    private String desc;

    TmpOrderExecuteStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static VAOrderTradeStatusEnum forValue(Integer value) {
        for (VAOrderTradeStatusEnum accountOrderEnum : VAOrderTradeStatusEnum.values()) {
            if (Objects.equals(accountOrderEnum.getValue(), value)) {
                return accountOrderEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
