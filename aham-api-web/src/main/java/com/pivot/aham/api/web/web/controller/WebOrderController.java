package com.pivot.aham.api.web.web.controller;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.pivot.aham.api.server.dto.req.UserGoalOrderDTO;
import com.pivot.aham.api.server.dto.res.UserGoalOrderResDTO;
import com.pivot.aham.api.server.remoteservice.OrderServiceRemoteService;
import com.pivot.aham.api.web.web.vo.req.UserOrdersReqVo;
import com.pivot.aham.api.web.web.vo.res.UserOrdersResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author luyang.li
 * @date 18/12/9
 * <p>
 * 提供给FE的用户订单接口
 */
@RestController
@RequestMapping("/app/")
@Api(value = "用户资产", description = "提供给FE的用户订单接口")
public class WebOrderController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebOrderController.class);
    @Resource
    private OrderServiceRemoteService orderServiceRemoteService;

    @PostMapping("user/order.api")
    @ApiOperation(value = "用户订单", produces = MediaType.APPLICATION_JSON_VALUE, notes
            = "订单接口需要以下1个参数：\n" + "1.用户clientId\n")
    public Message<List<UserOrdersResVo>> userBaseInfo(@RequestBody UserOrdersReqVo userOrdersReqVo) throws Exception {
        LOGGER.info("用户订单,请求参数,userOrdersReqVo:{}", JSON.toJSON(userOrdersReqVo));
        UserGoalOrderDTO userGoalOrderDTO = userOrdersReqVo.convertToDto(userOrdersReqVo);
        RpcMessage<List<UserGoalOrderResDTO>> rpcMessage = orderServiceRemoteService.queryUserGoalOrders(userGoalOrderDTO);
        List<UserOrdersResVo> userOrdersResVos = Lists.newArrayList();
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            if (CollectionUtils.isNotEmpty(rpcMessage.getContent())) {
                userOrdersResVos = rpcMessage.getContent().stream().map(item -> {
                    UserOrdersResVo vo = new UserOrdersResVo();
                    vo.setClientId(item.getClientId());
                    vo.setOrderType(item.getOrderType());
                    vo.setOrderTime(item.getOrderTime());
                    vo.setMoney(item.getMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
                    vo.setOrderNo(item.getOrderNo());
                    vo.setGoalId(item.getGoalId());
                    vo.setOrderStatus(item.getOrderStatus());
                    if (item.getOrderType() == 1) {
                        vo.setOrderTypeDesc("investment");
                    } else {
                        vo.setOrderTypeDesc("withdrawal");
                    }
                    switch (item.getOrderStatus()) {
                        case 1:
                            vo.setOrderStatusDesc("Processing");
                            break;
                        case 2:
                            vo.setOrderStatusDesc("Completed");
                            break;
                        default:
                            vo.setOrderStatusDesc("Failed");
                            break;
                    }
                    return vo;
                }).collect(Collectors.toList());
            }
        }
        LOGGER.info("用户订单,完成,UserAssetsResVo:{}", JSON.toJSON(userOrdersResVos));
        return Message.success(userOrdersResVos);
    }
}
