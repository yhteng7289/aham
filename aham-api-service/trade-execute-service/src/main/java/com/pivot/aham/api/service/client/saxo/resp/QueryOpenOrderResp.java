package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

import java.util.List;

/**
 * Created by hao.tong on 2018/12/25.
 */
@Data
public class QueryOpenOrderResp {
    private List<QueryOpenOrderItem> data;
}
