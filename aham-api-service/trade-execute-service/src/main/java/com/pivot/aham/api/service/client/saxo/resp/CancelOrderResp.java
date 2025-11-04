package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

import java.util.List;

@Data
public class CancelOrderResp {

    private List<PlaceNewOrderResp> orders;
}
