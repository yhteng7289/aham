package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 提现执行状态
 *
 * @author addison
 * @since 2018年12月11日
 */
public enum SaxoToUobTransferStatusEnum implements IEnum {
    WAITAPPLY(1, "待请中"),
    EXCHANGEREADY(11,"购汇申请准备"),
    APPLYING(2, "申请中"),
    APPLYFAIL(3, "申请失败"),
    APPLYSUCCESS(4, "申请成功"),
    ;

    private Integer value;
    private String desc;

    SaxoToUobTransferStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static SaxoToUobTransferStatusEnum forValue(Integer value) {
        for (SaxoToUobTransferStatusEnum redeemStatusEnum : SaxoToUobTransferStatusEnum.values()) {
            if (Objects.equals(redeemStatusEnum.getValue(), value)) {
                return redeemStatusEnum;
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
