package com.pivot.aham.api.service;

/**
 * Created by hao.tong on 2018/12/26.
 */
public interface SaxoTradingService {

    void mergeOrder();

    void sellOrBuy();

    void sell();

    void reviseShareSell();

    //void revisitPrice_sell();

    //void revisitMarket_sell();

    void reviseCancel();

    void tradeConfirmSell();

    void tradeConfirmSellOrBuy();


    void demergeOrderSell();

    void demergeOrderSellOrBuy();


    void recalculate();

    void buy();

    void reviseShareBuy();

    //void revisitPrice_buy();

    //void revisitMarket_buy();

    void tradeConfirmBuy();

    void demergeOrderBuy();

    void finishNotify();

    //void saveTwapPrice();


}
