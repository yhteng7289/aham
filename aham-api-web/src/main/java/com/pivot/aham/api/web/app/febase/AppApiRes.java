package com.pivot.aham.api.web.app.febase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author YYYz
 */
@Data
public class AppApiRes {
    @JsonProperty(value = "content")
    private String content;
    @JsonProperty(value = "errmsg")
    private String errMsg;
    @JsonProperty(value = "resultcode")
    private String resultCode;
    @JsonProperty(value = "timestamp")
    private String timestamp;

    public boolean isSuccess() {
        return "200".equals(resultCode);
    }
}
