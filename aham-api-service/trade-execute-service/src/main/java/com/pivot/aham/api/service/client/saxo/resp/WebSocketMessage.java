package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

@Data
public class WebSocketMessage {
    private long messageId;
    private String referenceId;
    private String payload;
}
