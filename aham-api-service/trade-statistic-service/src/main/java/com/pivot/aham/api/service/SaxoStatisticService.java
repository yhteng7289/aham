package com.pivot.aham.api.service;

import java.util.Date;

public interface SaxoStatisticService {
    /**
     * 分红
     * @param loadDate
     */
    void shareDividEnd(Date loadDate);

    /**
     * 总资产对账
     * @param date
     */
    void totalStatisEnd(Date date);

    /**
     * 现金记录
     */
    void recordBookkeepingCash();

    /**
     * 交易对账
     */
    void statisShareTrades();

    /**
     * etf持仓对账
     */
    void statisShareOpenPositions();

    /**
     * 记录现金流水
     */
    void recordCashTransactions();
}
