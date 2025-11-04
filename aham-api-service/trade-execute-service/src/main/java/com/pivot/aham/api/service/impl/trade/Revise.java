package com.pivot.aham.api.service.impl.trade;

import com.google.common.collect.Lists;
import com.pivot.aham.api.service.client.saxo.SaxoClient;
import com.pivot.aham.api.service.client.saxo.resp.OrderActivitiesResp;
import com.pivot.aham.api.service.client.saxo.resp.QueryOpenOrderItem;
import com.pivot.aham.api.service.client.saxo.resp.QueryOpenOrderResp;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-07-02 15:36
 **/

@Component
@Slf4j
public class Revise {

    public void reviseCancel() {
        try {
            QueryOpenOrderResp resp = SaxoClient.queryAllOpenOrder();
            if (resp != null && !CollectionUtils.isEmpty(resp.getData())) {
                for (QueryOpenOrderItem saxoOrder : resp.getData()) {
                    List<String> orderLogStatus = Lists.newArrayList(OrderActivitiesResp.OrderLogStatus.Fill, OrderActivitiesResp.OrderLogStatus.FinalFill);
                    OrderActivitiesResp activitiesResp = SaxoClient.queryOrderActivities(saxoOrder.getOrderId(), orderLogStatus);

                    if (!activitiesResp.haveFinalFill()) {
                        SaxoClient.cancelOrder(saxoOrder.getOrderId());
                    }
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
    }
}
