package com.pivot.aham.api.service.client.rest.resp;

import lombok.Data;

@Data
public class AhamTokenResp {
    private String access_token;
    private String token_type;
}
