package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by hao.tong on 2018/12/25.
 */
@Data
public class PriceInfoResp {
    private String assetType;
    private String lastUpdated;
    private String priceSource;
    private Integer uic;
    private Commissions commissions;
    private MarketDepth marketDepth;
    private PriceInfo priceInfo;
    private PriceInfoDetails priceInfoDetails;
    private Quote Quote;

    @Data
    public class Commissions{
        private BigDecimal costBuy;
        private BigDecimal costSell;
    }

    @Data
    public class MarketDepth{
        private BigDecimal[] ask;
        private BigDecimal[] askOrders;
        private BigDecimal[] askSize;
        private BigDecimal[] bid;
        private BigDecimal[] bidOrders;
        private BigDecimal[] bidSize;
        private Integer noOfBids;
        private Integer noOfOffers;
        private Boolean usingOrders;
    }

    @Data
    public class PriceInfo{
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal netChange;
        private BigDecimal percentChange;
    }

    @Data
    public class PriceInfoDetails{
        private BigDecimal askSize;
        private BigDecimal bidSize;
        private BigDecimal lastClose;
        private BigDecimal lastTraded;
        private BigDecimal lastTradedSize;
        private BigDecimal open;
        private BigDecimal volume;
    }

    @Data
    public class Quote{
        private Integer amount;
        private BigDecimal ask;
        private BigDecimal bid;
        private Integer delayedByMinutes;
        private String errorCode;
        private BigDecimal mid;
        private String priceTypeAsk;
        private String priceTypeBid;
    }
}
