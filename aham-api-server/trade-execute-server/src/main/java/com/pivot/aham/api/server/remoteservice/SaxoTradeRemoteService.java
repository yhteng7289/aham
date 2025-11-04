package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.req.*;
import com.pivot.aham.api.server.dto.resp.*;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by hao.tong on 2018/12/10.
 */
public interface SaxoTradeRemoteService extends BaseRemoteService {

    /**
     * saxo买单
     * @param saxoTradeReq
     * @return
     */
    RpcMessage<SaxoTradeResult> buy(SaxoTradeReq saxoTradeReq);

    /**
     * saxo卖单
     * @param saxoTradeReq
     * @return
     */
    RpcMessage<SaxoTradeResult> sell(SaxoTradeReq saxoTradeReq);

    RpcMessage<ClosingPriceResult> queryClosingPrice(ClosingPriceReq closingPriceReq);

    RpcMessage<BigDecimal> queryClosingPrice(String etfCode);

    RpcMessage<ExchangeRateResult> queryExchangeRate(ExchangeRateReq exchangeRateReq);

    RpcMessage<InterAccountTransferResult> transferToUSDFromSGD(InterAccountTransferReq interAccountTransferReq);

    RpcMessage<InterAccountTransferResult> transferToSGDFromUSD(InterAccountTransferReq interAccountTransferReq);

    RpcMessage<EtfHoldingResult> queryEtfHolding(EtfHoldingReq etfHoldingReq);

    /**
     * 查询实时汇率
     *
     * @return
     */
    RpcMessage<ExchangeRateResult> queryActualTimeRate();



    RpcMessage<TradingEnableResult> tradingEnable();

    RpcMessage<Map<Long, SaxoStatisShareTradesDTO>> statisShareTreadesExecute(Date nowDate);
}
