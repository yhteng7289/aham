//package com.pivot.aham.common.enums.analysis;
//
//import com.baomidou.mybatisplus.enums.IEnum;
//import EtfOrderTypeInnerEnum;
//
//import java.util.Objects;
//
///**
// * 请填写类注释
// *
// * @author addison
// * @since 2018年12月13日
// */
//public enum TmpOrderTadeTypeEnum  implements IEnum {
////    SELL(1, "提现"),
////    BUY(2, "购买"),
////    REBALANCE(3, "调仓")
//    RSA(1, "再平衡全部卖出", "X1", "XS1", 1,EtfOrderTypeInnerEnum.ALLREDEEM),
//    GSA(2, "一般全部卖出", "X2", "XS2", 2, EtfOrderTypeInnerEnum.ALLREDEEM),
//    RSP(3, "再平衡部分卖出", "Y1", "YS1", 3, EtfOrderTypeInnerEnum.PARTREDEEM),
//    GSP(4, "一般卖出", "Y2", "YS2", 4, EtfOrderTypeInnerEnum.PARTREDEEM),
//    GBA(5, "一般买单", "Z2", "ZS2", 5, EtfOrderTypeInnerEnum.BUY),
//    RBA(6, "再平衡买单", "Z1", "ZS1", 6, EtfOrderTypeInnerEnum.BUY),
//    PFT(7, "零散账户", "X3", "XS3", 7, EtfOrderTypeInnerEnum.PFT),
//    DO_NOTHING(8, "do nothing", "", "", 8, EtfOrderTypeInnerEnum.DO_NOTHING),
//    ;
//    private Integer value;
//    private String desc;
//
//    TmpOrderTadeTypeEnum(int value, String desc) {
//        this.value = value;
//        this.desc = desc;
//    }
//
//
//    public static VAOrderTradeStatusEnum forValue(Integer value) {
//        for (VAOrderTradeStatusEnum accountOrderEnum : VAOrderTradeStatusEnum.values()) {
//            if (Objects.equals(accountOrderEnum.getValue(), value)) {
//                return accountOrderEnum;
//            }
//        }
//        return null;
//    }
//
//
//    @Override
//    public Integer getValue() {
//        return value;
//    }
//
//    public String getDesc() {
//        return desc;
//    }
//}
