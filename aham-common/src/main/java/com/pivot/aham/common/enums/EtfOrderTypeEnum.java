package com.pivot.aham.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.util.Objects;

public enum EtfOrderTypeEnum implements IEnum {

    RSA(1, "再平衡全部卖出", "X1", "XS1", 1, EtfOrderTypeInnerEnum.ALLREDEEM),
    GSA(2, "一般全部卖出", "X2", "XS2", 2, EtfOrderTypeInnerEnum.ALLREDEEM),
    RSP(3, "再平衡部分卖出", "Y1", "YS1", 3, EtfOrderTypeInnerEnum.PARTREDEEM),
    GSP(4, "一般卖出", "Y2", "YS2", 4, EtfOrderTypeInnerEnum.PARTREDEEM),    
    GBA(5, "一般买单", "Z2", "ZS2", 5, EtfOrderTypeInnerEnum.BUY),
    RBA(6, "再平衡买单", "Z1", "ZS1", 6, EtfOrderTypeInnerEnum.BUY),
    PFT(7, "零散账户", "X3", "XS3", 7, EtfOrderTypeInnerEnum.PFT),    
    DO_NOTHING(8, "do nothing", "", "", 8, EtfOrderTypeInnerEnum.DO_NOTHING),;
    

    private int value;
    private String desc;
    private String amountType;
    private String shareType;
    private int seq;
    private EtfOrderTypeInnerEnum handleGroup;

    EtfOrderTypeEnum(int value, String desc, String amountType, String shareType, int seq, EtfOrderTypeInnerEnum handleGroup) {
        this.value = value;
        this.desc = desc;
        this.amountType = amountType;
        this.shareType = shareType;
        this.seq = seq;
        this.handleGroup = handleGroup;
    }

    public static EtfOrderTypeEnum forValue(Integer value) {
        for (EtfOrderTypeEnum investTypeEnum : EtfOrderTypeEnum.values()) {
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

    public String getAmountType() {
        return amountType;
    }

    public String getShareType() {
        return shareType;
    }

    public int getSeq() {
        return seq;
    }

    public EtfOrderTypeInnerEnum getHandleGroup() {
        return handleGroup;
    }

}
