package com.pivot.aham.common.enums.analysis;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

/**
 * Created by luyang.li on 2018/12/24.
 */
public enum DividendHandelStatusEnum implements IEnum {
    DEFAULT(0, "未处理"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败")
    ;

    DividendHandelStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;
    private String desc;

    @Override
    public Serializable getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
