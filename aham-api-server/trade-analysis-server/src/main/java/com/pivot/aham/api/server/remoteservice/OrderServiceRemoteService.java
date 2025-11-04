package com.pivot.aham.api.server.remoteservice;

import com.alibaba.fastjson.JSONArray;
import com.pivot.aham.api.server.dto.req.UserGoalOrderDTO;
import com.pivot.aham.api.server.dto.res.UserGoalOrderResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.Date;
import java.util.List;


/**
 * Created by luyang.li on 18/12/2.
 */
public interface OrderServiceRemoteService extends BaseRemoteService {

    /**
     * 查询goal上的订单
     *
     * @param userGoalOrderDTO
     * @return
     */
    RpcMessage<List<UserGoalOrderResDTO>> queryUserGoalOrders(UserGoalOrderDTO userGoalOrderDTO);

    RpcMessage<JSONArray> findRedeemApplyPage(Date startConfirmTime, Date endConfirmTime);
}
