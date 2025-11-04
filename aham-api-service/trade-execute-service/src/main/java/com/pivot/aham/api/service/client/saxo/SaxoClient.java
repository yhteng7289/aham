package com.pivot.aham.api.service.client.saxo;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.pivot.aham.api.service.client.saxo.resp.CancelOrderResp;
import com.pivot.aham.api.service.client.saxo.resp.HoldingInstrumentResp;
import com.pivot.aham.api.service.client.saxo.resp.AccountFundingResp;
import com.pivot.aham.api.service.client.saxo.resp.AccountFundingRespV2;
import com.pivot.aham.api.service.client.saxo.resp.ExchangeInfoResp;
import com.pivot.aham.api.service.client.saxo.resp.InterAccountTransferResp;
import com.pivot.aham.api.service.client.saxo.resp.OrderActivitiesResp;
import com.pivot.aham.api.service.client.saxo.resp.PlaceNewOrderResp;
import com.pivot.aham.api.service.client.saxo.resp.PositionDetailResp;
import com.pivot.aham.api.service.client.saxo.resp.PositionInfoResp;
import com.pivot.aham.api.service.client.saxo.resp.PriceInfoResp;
import com.pivot.aham.api.service.client.saxo.resp.QueryOpenOrderResp;
import com.pivot.aham.api.service.client.saxo.resp.WebSocketMessage;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.exception.HttpClientException;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.ExceptionUtil;
import com.pivot.aham.common.core.util.HttpResMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hao.tong on 2018/12/21.
 */
@Slf4j
public class SaxoClient extends SaxoClientBase {

    private static String createErrorMsg(String title, Object request, Object response) {
        return title + ", 【request】: " + JSON.toJSON(request) + ", 【response】: " + JSON.toJSON(response);
    }

    /**
     * 下一个卖单限价单
     *
     * @param uic saxo的股票ID
     * @param share 份额
     * @param price 价格
     * @return PlaceNewOrderResp
     */
    public static PlaceNewOrderResp placeSellLimitOrder(Integer uic, Integer share, BigDecimal price, boolean allowTooLarge) {
        return placeSellOrder(SaxoStaticParam.OrderType.Limit, uic, share, price, allowTooLarge);
    }

    /**
     * 下一个卖单市价单
     *
     * @param uic saxo的股票ID
     * @param share 份额
     * @return PlaceNewOrderResp
     */
    public static PlaceNewOrderResp placeSellMarketOrder(Integer uic, Integer share, boolean allowTooLarge) {
        return placeSellOrder(SaxoStaticParam.OrderType.Market, uic, share, null, allowTooLarge);
    }

    /**
     * 下一个买单限价单
     *
     * @param uic saxo的股票ID
     * @param share 份额
     * @param price 价格
     * @return PlaceNewOrderResp
     */
    public static PlaceNewOrderResp placeBuyLimitOrder(Integer uic, Integer share, BigDecimal price, boolean allowTooLarge) {
        return placeBuyOrder(SaxoStaticParam.OrderType.Limit, uic, share, price, allowTooLarge);
    }

    /**
     * 下一个买单市价单
     *
     * @param uic saxo的股票ID
     * @param share 份额
     * @return PlaceNewOrderResp
     */
    public static PlaceNewOrderResp placeBuyMarketOrder(Integer uic, Integer share, boolean allowTooLarge) {
        return placeBuyOrder(SaxoStaticParam.OrderType.Market, uic, share, null, allowTooLarge);
    }

    /**
     * 下一个卖单
     *
     * @param orderType 订单类型
     * @param uic saxo的股票ID
     * @param share 份额
     * @param price 价格
     * @return PlaceNewOrderResp
     */
    private static PlaceNewOrderResp placeSellOrder(String orderType, Integer uic, Integer share, BigDecimal price, boolean allowTooLarge) {
        return placeNewOrder(SaxoStaticParam.BuySellType.Sell, orderType, uic, share, price, allowTooLarge);
    }

    /**
     * 下一个买单
     *
     * @param orderType 订单类型
     * @param uic saxo的股票ID
     * @param share 份额
     * @param price 价格
     * @return PlaceNewOrderResp
     */
    private static PlaceNewOrderResp placeBuyOrder(String orderType, Integer uic, Integer share, BigDecimal price, boolean allowTooLarge) {
        return placeNewOrder(SaxoStaticParam.BuySellType.Buy, orderType, uic, share, price, allowTooLarge);
    }

    /**
     * 下单
     *
     * @param buySell 买/卖
     * @param orderType 订单类型
     * @param uic saxo的股票ID
     * @param share 份额
     * @param price 价格
     * @return PlaceNewOrderResp
     */
    private static PlaceNewOrderResp placeNewOrder(String buySell, String orderType, Integer uic, Integer share, BigDecimal price, boolean allowTooLarge) {
        String url = SaxoConstants.getBaseUrl() + "/trade/v2/orders/";

        Map<String, Object> orderDuration = Maps.newHashMap();
        orderDuration.put("DurationType", SaxoStaticParam.DurationType);

        Date start = DateUtils.nowUTC();
        Date end = DateUtils.addMinutes(start, 80);

        Map<String, Object> algoOrderDataArguments = Maps.newHashMap();
        algoOrderDataArguments.put("StartTime", DateUtils.getUTCTimeStr(start));
        algoOrderDataArguments.put("EndTime", DateUtils.getUTCTimeStr(end));

        Map<String, Object> requestBody = Maps.newHashMap();
        requestBody.put("AccountKey", SaxoConstants.getUSDAccountKey());
        requestBody.put("Amount", share);
        requestBody.put("AssetType", SaxoStaticParam.AssetType.Stock);
        requestBody.put("BuySell", buySell);
        requestBody.put("OrderDuration", orderDuration);

        if (isProd()) {
            Map<String, Object> algoOrderData = Maps.newHashMap();
            algoOrderData.put("StrategyName", "TWAP");
            algoOrderData.put("Arguments", algoOrderDataArguments);
            requestBody.put("AlgoOrderData", algoOrderData);
        }

        if (SaxoStaticParam.OrderType.Limit.equals(orderType)) {
            if (price != null) {
                requestBody.put("OrderPrice", price);
            }
        }

        requestBody.put("OrderType", orderType);
        requestBody.put("Uic", uic);

        HttpResMsg response = executePost(url, requestBody);
        if (response != null && !StringUtils.isEmpty(response.getResponseStr())) {
            PlaceNewOrderResp resp = JSON.parseObject(response.getResponseStr(), PlaceNewOrderResp.class);
            if (response.isSuccess() || (allowTooLarge && resp.isTooLarge())) {
                return resp;
            }
        }

        ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("下单失败", requestBody, response));
        throw new HttpClientException("下单失败");
    }

    public static PlaceNewOrderResp reviseMarketShare(String orderCode, Integer share) {
        return reviseOrder(orderCode, SaxoStaticParam.OrderType.Market, share, null);
    }

    public static PlaceNewOrderResp revisePrice(String orderCode, Integer share, BigDecimal price) {
        return reviseOrder(orderCode, SaxoStaticParam.OrderType.Limit, share, price);
    }

    public static PlaceNewOrderResp reviseToMarket(String orderCode) {
        return reviseOrder(orderCode, SaxoStaticParam.OrderType.Market, null, null);
    }

    public static PlaceNewOrderResp reviseOrder(String orderCode, String orderType, Integer share, BigDecimal price) {
        String url = SaxoConstants.getBaseUrl() + "/trade/v2/orders/";

        Map<String, Object> orderDuration = Maps.newHashMap();
        orderDuration.put("DurationType", SaxoStaticParam.DurationType);

        Map<String, Object> requestBody = Maps.newHashMap();
        requestBody.put("AccountKey", SaxoConstants.getUSDAccountKey());
        requestBody.put("OrderId", orderCode);
        requestBody.put("OrderType", orderType);
        requestBody.put("AssetType", SaxoStaticParam.AssetType.Stock);
        requestBody.put("OrderDuration", orderDuration);

        if (share != null) {
            requestBody.put("Amount", share);
        }

        if (price != null) {
            requestBody.put("OrderPrice", price);
        }

        HttpResMsg response = executePatch(url, requestBody);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), PlaceNewOrderResp.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("更新订单失败", requestBody, response));
            throw new HttpClientException("更新订单失败");
        }
    }

    public static CancelOrderResp cancelOrder(String orderCode) {
        String url = SaxoConstants.getBaseUrl() + "/trade/v2/orders/" + orderCode;

        Map<String, String> requestBody = Maps.newHashMap();
        requestBody.put("AccountKey", SaxoConstants.getUSDAccountKey());

        HttpResMsg response = executeDelete(url, requestBody);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), CancelOrderResp.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("取消订单失败", requestBody, response));
            throw new HttpClientException("取消订单失败");
        }
    }

    /**
     * 获取实时股票卖价
     *
     * @param uic saxo的股票ID
     * @return 价格
     */
    public static BigDecimal getEstimatedPrice_sell(Integer uic) {
        PriceInfoResp resp = queryPrice(uic, SaxoStaticParam.AssetType.Stock);
        return resp.getMarketDepth().getBid()[0];
    }

    /**
     * 获取实时股票买价
     *
     * @param uic saxo的股票ID
     * @return 价格
     */
    public static BigDecimal getEstimatedPrice_buy(Integer uic) {
        PriceInfoResp resp = queryPrice(uic, SaxoStaticParam.AssetType.Stock);
        return resp.getMarketDepth().getAsk()[0];
    }

    /**
     * 获取实时汇率
     *
     * @return USD->SGD 汇率
     */
    public static BigDecimal getExchangeRateUsdToSgd() {
        PriceInfoResp resp = queryPrice(45, SaxoStaticParam.AssetType.FxSpot);
        return resp.getQuote().getMid();
    }

    /**
     * 查询价格
     *
     * @param uic saxo的股票ID
     * @return PriceInfoResp
     */
    public static PriceInfoResp queryPrice(Integer uic, String assetType) {
        String url = SaxoConstants.getBaseUrl() + "/trade/v1/infoprices/";

        Map<String, String> params = Maps.newHashMap();
        params.put("AccountKey", SaxoConstants.getUSDAccountKey());
        params.put("AssetType", assetType);
        params.put("FieldGroups", SaxoStaticParam.FieldGroups);
        params.put("Uic", String.valueOf(uic));

        HttpResMsg response = executeGet(url, params);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), PriceInfoResp.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("价格查询失败", params, response));
            throw new HttpClientException("价格查询失败");
        }
    }

    public static boolean isAllOpenExchange(String exchangeCode, Date date) {
        return SaxoClient.queryExchangeOpenTime(exchangeCode, date) >= SaxoConstants.ExchangeOpenTime;
    }

    /**
     * 获取交易所当天开盘时间
     *
     * @param exchangeCode 交易所code
     * @param utcDate 日期（UTC）
     * @return 开盘时间（分钟）
     */
    public static Long queryExchangeOpenTime(String exchangeCode, Date utcDate) {
        String dateStr = DateFormatUtils.format(utcDate, DateUtils.DATE_FORMAT);
        ExchangeInfoResp resp = queryExchangeInfo(exchangeCode);
        DateFormat format = new SimpleDateFormat(DateUtils.DATE_TIME_FORMAT_UTC);

        try {
            for (ExchangeInfoResp.ExchangeSession exchangeSession : resp.getExchangeSessions()) {
                if ("AutomatedTrading".equals(exchangeSession.getState())) {
                    if (exchangeSession.getStartTime().startsWith(dateStr) && exchangeSession.getEndTime().startsWith(dateStr)) {
                        long startTime = format.parse(exchangeSession.getStartTime()).getTime();
                        long endTime = format.parse(exchangeSession.getEndTime()).getTime();
                        return (endTime - startTime) / (1000 * 60);
                    }
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("查询开盘时间失败", ExceptionUtil.getStackTraceAsString(e), resp));
            throw new BusinessException("查询开盘时间失败");
        }

        return 0L;
    }

    /**
     * 查询交易所信息
     *
     * @param exchangeCode 交易所code
     * @return ExchangeInfoResp
     */
    public static ExchangeInfoResp queryExchangeInfo(String exchangeCode) {
        String url = SaxoConstants.getBaseUrl() + "/ref/v1/exchanges/" + exchangeCode;

        HttpResMsg response = executeGet(url, Maps.newHashMap());
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), ExchangeInfoResp.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("查询交易所信息失败", exchangeCode, response));
            throw new HttpClientException("查询交易所信息失败");
        }
    }

    /**
     * 查询订单动作列表
     *
     * @param orderCode saxo的订单id
     * @return OrderActivitiesResp
     */
    public static OrderActivitiesResp queryOrderActivities(String orderCode, List<String> orderLogStatus) {
        String url = SaxoConstants.getBaseUrl() + "/cs/v1/audit/orderactivities/";

        Map<String, String> params = Maps.newHashMap();
        params.put("AccountKey", SaxoConstants.getUSDAccountKey());
        params.put("OrderId", orderCode);
        if (!CollectionUtils.isEmpty(orderLogStatus)) {
            params.put("Status", StringUtils.join(orderLogStatus, ","));
        }

        HttpResMsg response = executeGet(url, params);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), OrderActivitiesResp.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("订单Activities查询失败", params, response));
            throw new HttpClientException("订单Activities查询失败");
        }
    }

    /**
     * 根据ID查询当前持有
     *
     * @param uic saxo的股票ID
     * @return HoldingInstrumentResp
     */
    public static HoldingInstrumentResp queryHoldingInstrument(Integer uic) {
        List<HoldingInstrumentResp> respList = queryAllHoldingInstrument();
        if (!CollectionUtils.isEmpty(respList)) {
            for (HoldingInstrumentResp resp : respList) {
                if (uic.equals(resp.getUic())) {
                    return resp;
                }
            }
        }
        return new HoldingInstrumentResp(uic);
    }

    /**
     * 查询所有的持有资产
     *
     * @return 持有资产list
     */
    public static List<HoldingInstrumentResp> queryAllHoldingInstrument() {
        String url = SaxoConstants.getBaseUrl() + "/port/v1/exposure/instruments/";

        Map<String, String> params = Maps.newHashMap();
        params.put("AccountKey", SaxoConstants.getUSDAccountKey());
        params.put("ClientKey", SaxoConstants.getClientKey());

        HttpResMsg response = executeGet(url, params);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), new TypeToken<List<HoldingInstrumentResp>>() {
            }.getType());
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("查询当前持有失败" + url, params, response));
            throw new HttpClientException("查询当前持有失败");
        }
    }

    public static PositionInfoResp queryPositionInfo(String positionId) {
        String url = SaxoConstants.getBaseUrl() + "/port/v1/positions/" + positionId + "/";

        Map<String, String> params = Maps.newHashMap();
        params.put("AccountKey", SaxoConstants.getUSDAccountKey());
        params.put("ClientKey", SaxoConstants.getClientKey());
        params.put("PositionId", positionId);

        HttpResMsg response = executeGet(url, params);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), PositionInfoResp.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("查询PositionInfo失败" + url, params, response));
            throw new HttpClientException("查询Position失败");
        }
    }

    public static PositionDetailResp queryPositionDetail(String positionId) {
        String url = SaxoConstants.getBaseUrl() + "/port/v1/positions/" + positionId + "/details";

        Map<String, String> params = Maps.newHashMap();
        params.put("AccountKey", SaxoConstants.getUSDAccountKey());
        params.put("ClientKey", SaxoConstants.getClientKey());
        params.put("PositionId", positionId);

        HttpResMsg response = executeGet(url, params);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), PositionDetailResp.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("查询PositionDetail失败" + url, params, response));
            throw new HttpClientException("查询Position失败");
        }
    }

    public static InterAccountTransferResp transferToUSDFromSGD(BigDecimal amount) {
        if (isProd()) {
            return interAccountTransfer(amount, "SGD", SaxoConstants.getSGDAccountKey(), SaxoConstants.getUSDAccountKey());
        } else {
            InterAccountTransferResp resp = new InterAccountTransferResp();
            resp.setFromAccountCurrency("SGD");
            resp.setFromAccountAmount(amount);
            resp.setToAccountCurrency("USD");
            resp.setToAccountAmount(amount);
            return resp;
        }
    }

    public static InterAccountTransferResp transferToSGDFromUSD(BigDecimal amount) {
        if (isProd()) {
            return interAccountTransfer(amount, "USD", SaxoConstants.getUSDAccountKey(), SaxoConstants.getSGDAccountKey());
        } else {
            InterAccountTransferResp resp = new InterAccountTransferResp();
            resp.setFromAccountCurrency("USD");
            resp.setFromAccountAmount(amount);
            resp.setToAccountCurrency("SGD");
            resp.setToAccountAmount(amount.multiply(new BigDecimal(2)));
            return resp;
//            throw new HttpClientException("测试内部转账失败");
        }
    }

    private static InterAccountTransferResp interAccountTransfer(BigDecimal amount, String currency, String fromAccountKey, String toAccountKey) {
        String url = SaxoConstants.getBaseUrl() + "/cs/v2/cashmanagement/interaccounttransfers/";

        Map<String, Object> requestBody = Maps.newHashMap();
        requestBody.put("Amount", amount);
        requestBody.put("Currency", currency);
        requestBody.put("FromAccountKey", fromAccountKey);
        requestBody.put("ToAccountKey", toAccountKey);
        log.info("==================START SAXO CLIENT INTER-ACCOUNT TRANSFER==========================="); //Added WooiTatt
        HttpResMsg response = executePost(url, requestBody);
        log.info("==================END SAXO CLIENT INTER-ACCOUNT TRANSFER===========================");//Added WooiTatt
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), InterAccountTransferResp.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("内部转账失败" + url, requestBody, response));
            throw new HttpClientException("内部转账失败");
        }
    }

    private static List<WebSocketMessage> queryEns(String accountKey, String[] activityType, String sequenceId) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String contextId = "AF_" + uuid;
        String referenceId = "ref_1";

        List<WebSocketMessage> messageList = Lists.newArrayList();

        try {
            Map<String, String> httpHeaders = Maps.newHashMap();
            httpHeaders.put("Authorization", "Bearer " + getToken());
            final WebSocketClient webSocket = new WebSocketClient(new URI(SaxoConstants.getStreamingUrl() + "?contextid=" + contextId), httpHeaders) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    String create_url = SaxoConstants.getBaseUrl() + "/ens/v1/activities/subscriptions";

                    Map<String, Object> arguments = Maps.newHashMap();
                    arguments.put("AccountKey", accountKey);
                    arguments.put("ClientKey", SaxoConstants.getClientKey());
                    arguments.put("Activities", activityType);

                    if (StringUtils.isEmpty(sequenceId)) {
                        arguments.put("FromDateTime", "2018-10-01T00:00:00Z");
                    } else {
                        arguments.put("SequenceId", sequenceId);
                    }

                    Map<String, Object> requestBody = Maps.newHashMap();
                    requestBody.put("Arguments", arguments);
                    requestBody.put("ContextId", contextId);
                    requestBody.put("ReferenceId", referenceId);

                    HttpResMsg response = executePost(create_url, requestBody);
                    if (response == null || response.getStatusCode() != 201) {
                        ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("创建订阅失败" + create_url, requestBody, response));
                    }
                }

                @Override
                public void onMessage(String msg) {
                }

                @Override
                public void onMessage(ByteBuffer buffer) {
                    messageList.addAll(parseMessage(buffer));
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    String delete_url = SaxoConstants.getBaseUrl() + "/ens/v1/activities/subscriptions/" + contextId + "/" + referenceId;
                    Map<String, String> params = Maps.newHashMap();
                    HttpResMsg response = executeDelete(delete_url, params);
                    if (response == null || response.getStatusCode() != 202) {
                        ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("删除订阅失败" + delete_url, params, response));
                    }
                }

                @Override
                public void onError(Exception ex) {
                    ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("WebSocketClient.onError()", ExceptionUtil.getStackTraceAsString(ex), ""));
                }
            };

            webSocket.connectBlocking();

            int maxWaitTimes = 10;
            int waitTimes = 0;
            while (true) {
                if (waitTimes >= maxWaitTimes) {
                    return messageList;
                }
                Thread.sleep(1000 * 3);
                if (messageList.size() > 0) {
                    return messageList;
                }
                waitTimes++;
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, ExceptionUtil.getStackTraceAsString(e));
        }
        return messageList;
    }

    public static List<AccountFundingResp> queryAccountFundingEvent(String sequenceId) {
        List<AccountFundingResp> result = Lists.newArrayList();
        List<WebSocketMessage> messageList = queryEns(
                SaxoConstants.getUSDAccountKey(),
                new String[]{"AccountFundings"},
                sequenceId);
        for (WebSocketMessage message : messageList) {
            if (!"_heartbeat".equals(message.getReferenceId())) {
                List<AccountFundingResp> children = JSON.parseObject(message.getPayload(), new TypeToken<List<AccountFundingResp>>() {
                }.getType());
                if (children != null) {
                    for (AccountFundingResp accountFundingResp : children) {
                        if ("AccountFundings".equals(accountFundingResp.getActivityType())) {
                            result.add(accountFundingResp);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static AccountFundingRespV2 queryAccountFundingEventV2(String sequenceId) {
        return queryAccountFundingEventV2(SaxoConstants.getSGDAccountKey(), sequenceId);
    }

    public static AccountFundingRespV2 queryAccountFundingEventV2(String accountKey, String sequenceId) {
        String url = SaxoConstants.getBaseUrl() + "ens/v1/activities";

        Map<String, String> params = Maps.newHashMap();
        params.put("AccountKey", accountKey);
        params.put("ClientKey", SaxoConstants.getClientKey());
        params.put("Activities", "AccountFundings");
        if (StringUtils.isEmpty(sequenceId)) {
            Date start = DateUtils.addDays(DateUtils.now(), -13);
            params.put("FromDateTime", DateUtils.getUTCStr(start));
        } else {
            params.put("SequenceId", sequenceId);
        }

        HttpResMsg response = executeGet(url, params);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), AccountFundingRespV2.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("queryAccountFundingEventV2 fail" + url, params, response));
            throw new HttpClientException("queryAccountFundingEventV2 fail");
        }
    }

    public static AccountFundingRespV2 queryAccountAllEvent(String type, String dayBefore) {
        String url = SaxoConstants.getBaseUrl() + "ens/v1/activities";

        Map<String, String> params = Maps.newHashMap();
        params.put("AccountKey", SaxoConstants.getUSDAccountKey());
        params.put("ClientKey", SaxoConstants.getClientKey());
        params.put("Activities", type);

        if (dayBefore == null) {
            dayBefore = "10";
        }
        Integer minusDay = 0 - Integer.valueOf(dayBefore);

        Date start = DateUtils.addDays(DateUtils.now(), minusDay);
        params.put("FromDateTime", DateUtils.getUTCStr(start));

        HttpResMsg response = executeGet(url, params);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), AccountFundingRespV2.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("queryAccountFundingEventV2 fail" + url, params, response));
            throw new HttpClientException("queryAccountFundingEventV2 fail");
        }
    }

    public static QueryOpenOrderResp queryAllOpenOrder() {
        String url = SaxoConstants.getBaseUrl() + "/port/v1/orders/me";

        Map<String, String> params = Maps.newHashMap();

        HttpResMsg response = executeGet(url, params);
        if (response != null && response.isSuccess()) {
            return JSON.parseObject(response.getResponseStr(), QueryOpenOrderResp.class);
        } else {
            ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("获取所有生效的订单失败" + url, params, response));
            throw new HttpClientException("获取所有生效的订单失败");
        }
    }

    private static List<WebSocketMessage> parseMessage(ByteBuffer buffer) {
        byte[] message = buffer.array();
        List<WebSocketMessage> parsedMessages = Lists.newArrayList();
        int index = 0;
        do {
            //First 8 bytes make up the message id. A 64 bit integer.
            byte[] messageIdByte = new byte[8];
            System.arraycopy(message, index, messageIdByte, 0, 8);
            long messageId = bytesToLong(messageIdByte);
            index += 8;

            //Skip the next two bytes that contain a reserved field.
            index += 2;

            //1 byte makes up the reference id length as an 8 bit integer. The reference id has a max length og 50 chars.
            int referenceIdSize = message[index];
            index += 1;

            //n bytes make up the reference id. The reference id is an ASCII string.
            byte[] referenceIdByte = new byte[referenceIdSize];
            System.arraycopy(message, index, referenceIdByte, 0, referenceIdSize);
            String referenceId = getStrByByte(referenceIdByte, "ASCII");
            index += referenceIdSize;

            //1 byte makes up the payload format. The value 0 indicates that the payload format is Json.
            int payloadFormat = message[index];
            index++;

            //4 bytes make up the payload length as a 32 bit integer.
            byte[] payloadSizeByte = new byte[4];
            System.arraycopy(message, index, payloadSizeByte, 0, 4);
            int payloadSize = bytesToInt(payloadSizeByte);
            index += 4;

            //n bytes make up the actual payload. In the case of the payload format being Json, this is a UTF8 encoded string.
            byte[] payloadByte = new byte[payloadSize];
            System.arraycopy(message, index, payloadByte, 0, payloadSize);
            String payloadStr = getStrByByte(payloadByte, "UTF-8");
            index += payloadSize;

            WebSocketMessage webSocketMessage = new WebSocketMessage();
            webSocketMessage.setMessageId(messageId);
            webSocketMessage.setReferenceId(referenceId);
            webSocketMessage.setPayload(payloadStr);
            parsedMessages.add(webSocketMessage);
        } while (index < message.length);

        return parsedMessages;
    }

    private static long bytesToLong(byte[] input) {
        long value = 0;
        // 循环读取每个字节通过移位运算完成long的8个字节拼装
        for (int count = 0; count < 8; ++count) {
            int shift = count << 3;
            value |= ((long) 0xff << shift) & ((long) input[count] << shift);
        }
        return value;
    }

    private static int bytesToInt(byte[] input) {
        int value;
        value = ((input[0] & 0xFF)
                | ((input[1] << 8) & 0xFF00)
                | ((input[2] << 16) & 0xFF0000)
                | ((input[3] << 24) & 0xFF000000));
        return value;
    }

    private static String getStrByByte(byte[] payload, String charsetFormat) {
        Charset charset = Charset.forName(charsetFormat);
        ByteBuffer referenceIdByteBuffer = ByteBuffer.wrap(payload);
        return charset.decode(referenceIdByteBuffer).toString();
    }

    public static void interfaceTest() {
        String url = SaxoConstants.getBaseUrl() + "/port/v1/accounts/me";

        Map<String, String> params = Maps.newHashMap();

        HttpResMsg response = executeGet(url, params);

        System.out.println("");
    }
}
