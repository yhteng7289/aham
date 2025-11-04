package com.pivot.aham.common.enums;

public enum EtfOrderTypeInnerEnum {
    ALLREDEEM("allRedeem", 1),
    PARTREDEEM("partRedeem", 2),
    BUY("buy", 3),
    PFT("pft", 4),
    DO_NOTHING("", 5);


    private String value;
    private int seq;


    EtfOrderTypeInnerEnum(String value, int seq) {
        this.value = value;
        this.seq = seq;
    }

    public String getValue() {
        return value;
    }

    public int getSeq() {
        return seq;
    }
}