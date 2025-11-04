package com.pivot.aham.api.service;

import com.pivot.aham.api.server.dto.req.ClosingPriceReq;
import com.pivot.aham.api.server.dto.req.EtfHoldingReq;
import com.pivot.aham.api.server.dto.req.ExchangeRateReq;
import com.pivot.aham.api.server.dto.req.InterAccountTransferReq;
import com.pivot.aham.api.server.dto.resp.*;

import java.math.BigDecimal;

public interface TradingSupportService {

    TradingEnableResult tradingEnable();

    ClosingPriceResult queryClosingPrice(ClosingPriceReq closingPriceReq);

    BigDecimal queryLastClosingPrice(String etfCode);

    void saveClosingPrice(String bsnDtStr);

    void saveClosingPrice_all();

    ExchangeRateResult queryExchangeRate(ExchangeRateReq exchangeRateReq);

    void saveExchangeRate();

    InterAccountTransferResult transferToUSDFromSGD(InterAccountTransferReq interAccountTransferReq);

    InterAccountTransferResult transferToSGDFromUSD(InterAccountTransferReq interAccountTransferReq);

    void saveAccountFoundingEvent();

    EtfHoldingResult queryEtfHolding(EtfHoldingReq etfHoldingReq);

    ExchangeRateResult queryActualTimeRate();

    void errorFeeNotify();

    void saveAccountFoundingEventLog();
    
    void saveAhamClosingPrice(String bsndt);
}
