package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月06日
 */
public enum AssetSourceEnum implements IEnum {
    DEFAULT(-1, "默认"),
    CASHDIVIDEND(0, "现金分红"),
    STOCKDIVIDEND(1, "股票分红"),
    ETFSELL(2, "ETF卖出"),
    SAXOEXCHANGE(3, "SAXO购汇"),
    CASHWITHDRAWAL(4, "提现"),
    BUYRESIDUAL(5, "购买剩余"),
    RECHARGE(6, "充值"),
    BUYETF(7, "ETF购买"),
    BUYCROSS(8, "购买对冲"),
    SELLCROSS(9, "卖出对冲"),
    NORMALFEE(10, "手续费");

    private Integer value;
    private String desc;

    AssetSourceEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static AssetSourceEnum forValue(Integer value) {
        for (AssetSourceEnum accountOrderEnum : AssetSourceEnum.values()) {
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
