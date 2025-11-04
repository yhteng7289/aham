package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

import java.util.List;

@Data
public class AccountFundingRespV2 {
    private int __count;
    private List<AccountFundingResp> data;
}
