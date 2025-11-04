package com.pivot.aham.common.core.util;

import lombok.Data;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月25日
 */
@Data
public class HttpResMsg {

    private int statusCode;
    private String responseStr;

    public boolean isSuccess() {
        return statusCode == 200;
    }
}
