package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

/**
 * Created by hao.tong on 2018/12/25.
 */
@Data
public class QueryTokenResp {
    private String access_token;
    private String token_type;
    private Long expires_in;
    private String refresh_token;
    private Long refresh_token_expires_in;
    private String base_uri;
}
