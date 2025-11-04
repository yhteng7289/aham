package com.pivot.aham.api.web.app.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.req.UserGoalOrderDTO;
import com.pivot.aham.api.server.dto.res.UserGoalOrderResDTO;
import com.pivot.aham.api.server.remoteservice.OrderServiceRemoteService;
import com.pivot.aham.api.web.app.febase.AppResultCode;
import com.pivot.aham.api.web.app.vo.res.AppUserOrdersResVo;
import com.pivot.aham.api.web.web.vo.req.UserOrdersReqVo;
import com.pivot.aham.api.web.web.vo.res.UserOrdersResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.util.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YYYz
 */
@RestController
@RequestMapping("/api/v1/")
@Api(value = "订单信息", description = "订单信息接口")
public class AppOrderController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppOrderController.class);
    @Resource
    private OrderServiceRemoteService orderServiceRemoteService;

    @PostMapping("app/orders")
    @ApiOperation(value = "用户订单", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<List<AppUserOrdersResVo>> userBaseInfo(@RequestBody UserOrdersReqVo userOrdersReqVo) throws Exception {
        if (!checkLogin(userOrdersReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("用户订单,请求参数,userOrdersReqVo:{}", JSON.toJSON(userOrdersReqVo));
        UserGoalOrderDTO userGoalOrderDTO = userOrdersReqVo.convertToDto(userOrdersReqVo);
        RpcMessage<List<UserGoalOrderResDTO>> rpcMessage = orderServiceRemoteService.queryUserGoalOrders(userGoalOrderDTO);

        List<UserOrdersResVo> userOrdersResVos = com.beust.jcommander.internal.Lists.newArrayList();
        List<AppUserOrdersResVo> appUserOrdersResVoList = Lists.newArrayList();
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            if (CollectionUtils.isNotEmpty(rpcMessage.getContent())) {
                userOrdersResVos = rpcMessage.getContent().stream().map(item -> {
                    UserOrdersResVo vo = new UserOrdersResVo();
                    vo.setClientId(item.getClientId());
                    vo.setOrderType(item.getOrderType());
                    if (item.getOrderType() == 1) {
                        vo.setOrderTypeDesc("investment");
                    } else {
                        vo.setOrderTypeDesc("withdrawal");
                    }

                    vo.setOrderTime(item.getOrderTime());
                    vo.setMoney(item.getMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
                    vo.setOrderNo(item.getOrderNo());
                    vo.setGoalId(item.getGoalId());
                    vo.setOrderStatus(item.getOrderStatus());
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
                Map<String, List<UserOrdersResVo>> map = new HashMap<>();
                for (UserOrdersResVo userOrdersResVo : userOrdersResVos) {
                    Date date = DateUtils.parseDate(userOrdersResVo.getOrderTime(), DateUtils.DATE_TIME_FORMAT);
                    if (map.get(changeYMDtoEn(date)) == null) {
                        List<UserOrdersResVo> list = Lists.newArrayList();
                        list.add(userOrdersResVo);
                        map.put(changeYMDtoEn(date), list);
                    } else {
                        List<UserOrdersResVo> userOrdersResVoList = map.get(changeYMDtoEn(date));
                        userOrdersResVoList.add(userOrdersResVo);
                    }
                }
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd", Locale.UK);
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
                for (String date : map.keySet()) {
                    AppUserOrdersResVo appUserOrdersResVo = new AppUserOrdersResVo();
                    appUserOrdersResVo.setDate(date);
                    Date formattedDate = sdf2.parse(date);
                    String strDate = sdf1.format(formattedDate);
                    appUserOrdersResVo.setDateNumber(Integer.valueOf(strDate));
                    appUserOrdersResVo.setUserOrdersResVos(map.get(date));
                    appUserOrdersResVoList.add(appUserOrdersResVo);
                }
            }
        }

        appUserOrdersResVoList.sort((o1, o2) -> o2.getDateNumber().compareTo(o1.getDateNumber()));

        LOGGER.info("用户订单,完成,UserAssetsResVo:{}", JSON.toJSON(userOrdersResVos));
        return Message.success(appUserOrdersResVoList);
    }

    public String changeYMDtoEn(Date dateYMD) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
        return sdf.format(dateYMD);
    }

}
