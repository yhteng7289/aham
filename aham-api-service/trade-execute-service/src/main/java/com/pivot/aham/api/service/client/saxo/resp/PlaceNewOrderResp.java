package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

/**
 * Created by hao.tong on 2018/12/25.
 */
@Data
public class PlaceNewOrderResp {
    private String orderId;
    private ErrorInfo errorInfo;
    private Integer statusCode;
    private boolean success;

    @Data
    public class ErrorInfo{
        private String ErrorCode;
        private String Message;
    }

    public boolean isTooLarge() {
        return errorInfo != null && "OrderValueTooLarge".equals(errorInfo.getErrorCode());
    }
}
